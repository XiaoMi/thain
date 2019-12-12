/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.scheduler.job;

import com.xiaomi.thain.common.constant.FlowExecutionStatus;
import com.xiaomi.thain.common.exception.ThainCreateFlowExecutionException;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.common.model.dp.AddFlowExecutionDp;
import com.xiaomi.thain.core.constant.FlowExecutionTriggerType;
import com.xiaomi.thain.core.process.ProcessEngine;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.xiaomi.thain.common.utils.HostUtils.getHostInfo;

/**
 * @author liangyongrui
 */
@Slf4j
public class FlowJob implements Job {

    @NonNull
    private final ProcessEngine processEngine;

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
            val addFlowExecutionDp = AddFlowExecutionDp.builder()
                    .flowId(flowId)
                    .hostInfo(getHostInfo())
                    .status(FlowExecutionStatus.WAITING.code)
                    .triggerType(FlowExecutionTriggerType.AUTOMATIC.code)
                    .build();
            processEngine.processEngineStorage.flowExecutionDao.addFlowExecution(addFlowExecutionDp);
            if (addFlowExecutionDp.id == null) {
                throw new ThainCreateFlowExecutionException();
            }

            val flowExecutionDr = processEngine.processEngineStorage.flowExecutionDao
                    .getFlowExecution(addFlowExecutionDp.id).orElseThrow(ThainRuntimeException::new);
            processEngine.processEngineStorage.flowExecutionWaitingQueue.put(flowExecutionDr);
        } catch (Exception e) {
            log.error("Failed to add queue：", e);
        }
    }

}
