/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.xiaomi.thain.common.constant.FlowExecutionStatus;
import com.xiaomi.thain.core.process.ProcessEngine;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RecoveryQob implements Job {

    @NonNull
    private final ProcessEngine processEngine;

    private static final Map<String, RecoveryQob> RECOVERY_QOB_MAP = new ConcurrentHashMap<>();

    private RecoveryQob(@NonNull ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public static RecoveryQob getInstance(@NonNull ProcessEngine processEngine) {
        return RECOVERY_QOB_MAP.computeIfAbsent(processEngine.processEngineId, t -> new RecoveryQob(processEngine));
    }

    @Override
    public void execute(@NonNull JobExecutionContext context) {
        val flowExecutionDao = processEngine.processEngineStorage.flowExecutionDao;
        val jobExecutionDao = processEngine.processEngineStorage.jobExecutionDao;
        val flowExecutionDrList = flowExecutionDao.getDead();
        if (flowExecutionDrList.isEmpty()) {
            return;
        }
        val ids = flowExecutionDrList.stream().map(t -> t.id).collect(Collectors.toList());
        flowExecutionDao.reWaiting(ids);

        flowExecutionDrList.stream()
                .filter(t -> t.status == FlowExecutionStatus.RUNNING.code)
                .map(t -> t.flowId)
                .distinct()
                .forEach(processEngine.processEngineStorage.flowDao::killFlow);

        jobExecutionDao.deleteJobExecutionByFlowExecutionIds(ids);

        log.info("Scanned some dead flows: \n" + JSON.toJSONString(flowExecutionDrList));
        processEngine.processEngineStorage.flowExecutionWaitingQueue.addAll(flowExecutionDrList);
        val hostInfo = getHostInfo();
        flowExecutionDrList.forEach(t -> flowExecutionDao.updateHostInfo(t.id, hostInfo));
    }

}
