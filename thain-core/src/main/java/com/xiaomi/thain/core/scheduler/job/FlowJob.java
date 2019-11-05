/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.scheduler.job;

import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.exception.ThainFlowRunningException;
import com.xiaomi.thain.core.process.ProcessEngine;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liangyongrui
 */
@Log4j2
public class FlowJob implements Job {

    @NonNull
    private ProcessEngine processEngine;

    private static final Map<String, FlowJob> FLOW_JOB_MAP = new ConcurrentHashMap<>();

    private FlowJob(@NonNull ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public static FlowJob getInstance(@NonNull ProcessEngine processEngine) {
        return FLOW_JOB_MAP.computeIfAbsent(processEngine.processEngineId,
                t -> new FlowJob(processEngine));
    }

    @Override
    public void execute(@NonNull JobExecutionContext context) {
        try {
            long flowId = context.getJobDetail().getJobDataMap().getLong("flowId");
            log.info("auto execution: " + flowId);
            processEngine.schedulerStartProcess(flowId);
        } catch (ThainFlowRunningException e) {
            log.warn(ExceptionUtils.getRootCauseMessage(e));
        } catch (ThainException e) {
            log.error("Failed to auto trigger flow：", e);
        }
    }

}
