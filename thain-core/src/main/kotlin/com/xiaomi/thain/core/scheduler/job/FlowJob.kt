/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.scheduler.job

import com.xiaomi.thain.common.constant.FlowExecutionStatus
import com.xiaomi.thain.common.exception.ThainCreateFlowExecutionException
import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.common.model.dp.AddFlowExecutionDp
import com.xiaomi.thain.common.utils.HostUtils
import com.xiaomi.thain.core.constant.FlowExecutionTriggerType
import com.xiaomi.thain.core.process.ProcessEngine
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * @author liangyongrui
 */
class FlowJob private constructor(private val processEngine: ProcessEngine) : Job {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    override fun execute(context: JobExecutionContext) {
        try {
            val flowId = context.jobDetail.jobDataMap.getLong("flowId")
            val addFlowExecutionDp = AddFlowExecutionDp(
                    flowId = flowId,
                    hostInfo = HostUtils.hostInfo,
                    status = FlowExecutionStatus.WAITING.code,
                    triggerType = FlowExecutionTriggerType.AUTOMATIC.code,
                    variables = "{}")
            processEngine.processEngineStorage.flowExecutionDao.addFlowExecution(addFlowExecutionDp)
            if (addFlowExecutionDp.id == null) {
                throw ThainCreateFlowExecutionException()
            }
            val flowExecutionDr = processEngine.processEngineStorage.flowExecutionDao
                    .getFlowExecution(addFlowExecutionDp.id!!) ?: throw ThainRuntimeException()
            processEngine.processEngineStorage.flowExecutionWaitingQueue.put(flowExecutionDr)
            log.debug("flow {} add queue，There are currently {} flows in the queue",
                    flowId, processEngine.processEngineStorage.flowExecutionWaitingQueue.size)
        } catch (e: Exception) {
            log.error("Failed to add queue：", e)
        }
    }

    companion object {
        private val FLOW_JOB_MAP: MutableMap<String, FlowJob> = ConcurrentHashMap()

        @JvmStatic
        fun getInstance(processEngine: ProcessEngine): FlowJob {
            return FLOW_JOB_MAP.computeIfAbsent(processEngine.processEngineId
            ) { FlowJob(processEngine) }
        }
    }

}
