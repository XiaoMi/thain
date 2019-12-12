/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.scheduler.job;

import com.xiaomi.thain.common.constant.FlowExecutionStatus;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.core.process.ProcessEngine;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Date 19-7-16 上午10:59
 *
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
public class SlaJob implements Job {

    @NonNull
    private final ProcessEngine processEngine;

    private static final Map<String, SlaJob> SLA_JOB_MAP = new ConcurrentHashMap<>();

    private SlaJob(@NonNull ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public static SlaJob getInstance(@NonNull ProcessEngine processEngine) {
        return SLA_JOB_MAP.computeIfAbsent(processEngine.processEngineId, t -> new SlaJob(processEngine));
    }

    @SneakyThrows
    @Override
    public void execute(@NonNull JobExecutionContext context) {
        val dataMap = context.getJobDetail().getJobDataMap();
        long flowId = dataMap.getLong("flowId");
        long flowExecutionId = dataMap.getLong("flowExecutionId");
        val flowExecutionModel = processEngine.processEngineStorage.flowExecutionDao
                .getFlowExecution(flowExecutionId)
                .orElseThrow(() -> new ThainRuntimeException("flowExecution id does not exist：" + flowExecutionId));

        if (flowExecutionModel.status == FlowExecutionStatus.RUNNING.code) {
            try {
                val flow = processEngine.processEngineStorage.flowDao.getFlow(flowId)
                        .orElseThrow(() -> new ThainRuntimeException("flow does not exist， flowId:" + flowId));
                if (flow.getSlaKill()) {
                    processEngine.thainFacade.killFlowExecution(flowExecutionId, true);
                }
                if (StringUtils.isNotBlank(flow.getSlaEmail())) {
                    processEngine.processEngineStorage.mailService.send(
                            flow.getSlaEmail().trim().split(","),
                            "Thain SLA提醒",
                            "您的任务：" + flow.getName() + "(" + flow.getId() + "), 超出期望的执行时间"
                    );
                }
            } catch (Exception e) {
                log.error("kill failed, flowExecutionId:" + flowExecutionId, e);
            }
        }
        context.getScheduler().deleteJob(context.getJobDetail().getKey());
    }

}
