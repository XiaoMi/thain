/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.xiaomi.thain.core.process.ProcessEngine;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.xiaomi.thain.common.utils.HostUtils.getHostInfo;

/**
 * 恢复失败任务
 *
 * @author liangyongrui
 */
@Log4j2
public class RecoveryJob implements Job {

    @NonNull
    private ProcessEngine processEngine;

    private static final Map<String, RecoveryJob> CLEAN_JOB_MAP = new ConcurrentHashMap<>();

    private RecoveryJob(@NonNull ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public static RecoveryJob getInstance(@NonNull ProcessEngine processEngine) {
        return CLEAN_JOB_MAP.computeIfAbsent(processEngine.processEngineId, t -> new RecoveryJob(processEngine));
    }

    @Override
    public void execute(@NonNull JobExecutionContext context) {
        val flowExecutionDao = processEngine.processEngineStorage.flowExecutionDao;
        val flowExecutionDrList = flowExecutionDao.getDead();
        if (flowExecutionDrList.isEmpty()) {
            return;
        }
        val ids = flowExecutionDrList.stream().map(t -> t.id).collect(Collectors.toList());
        flowExecutionDao.reWaiting(ids);
        log.info("Scanned some dead flows: " + JSON.toJSONString(flowExecutionDrList));
        processEngine.processEngineStorage.flowExecutionWaitingQueue.addAll(flowExecutionDrList);
        val hostInfo = getHostInfo();
        flowExecutionDrList.forEach(t -> flowExecutionDao.updateHostInfo(t.id, hostInfo));
    }

}
