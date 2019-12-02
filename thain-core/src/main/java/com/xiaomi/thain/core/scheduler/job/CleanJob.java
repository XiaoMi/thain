/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.scheduler.job;

import com.xiaomi.thain.core.process.ProcessEngine;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liangyongrui
 */
@Slf4j
public class CleanJob implements Job {

    @NonNull
    private final ProcessEngine processEngine;

    private static final Map<String, CleanJob> CLEAN_JOB_MAP = new ConcurrentHashMap<>();

    private CleanJob(@NonNull ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public static CleanJob getInstance(@NonNull ProcessEngine processEngine) {
        return CLEAN_JOB_MAP.computeIfAbsent(processEngine.processEngineId, t -> new CleanJob(processEngine));
    }

    @Override
    public void execute(@NonNull JobExecutionContext context) {
        val flowExecutionDao = processEngine.processEngineStorage.flowExecutionDao;
        flowExecutionDao.cleanFlowExecution();
        val flowIds = processEngine.processEngineStorage.flowDao.getAllFlowIds();
        val needDeleteFlowExecutionIds = flowExecutionDao.getNeedDeleteFlowExecutionId(flowIds);
        flowExecutionDao.deleteFlowExecutionByIds(needDeleteFlowExecutionIds);

        val flowExecutionIds = flowExecutionDao.getAllFlowExecutionIds();
        val jobExecutionDao = processEngine.processEngineStorage.jobExecutionDao;
        val needDeleteJobExecutionIds = jobExecutionDao.getNeedDeleteJobExecutionIds(flowExecutionIds);
        jobExecutionDao.deleteJobExecutionByIds(needDeleteJobExecutionIds);
    }

}
