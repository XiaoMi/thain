/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.scheduler.job

import com.xiaomi.thain.common.constant.FlowExecutionStatus
import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.core.process.ProcessEngine
import org.apache.commons.lang3.StringUtils
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Date 19-7-16 上午10:59
 *
 * @author liangyongrui@xiaomi.com
 */
class SlaJob private constructor(private val processEngine: ProcessEngine) : Job {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    override fun execute(context: JobExecutionContext) {
        val dataMap = context.jobDetail.jobDataMap
        val flowId = dataMap.getLong("flowId")
        val flowExecutionId = dataMap.getLong("flowExecutionId")
        val (_, _, status) = processEngine.processEngineStorage.flowExecutionDao
                .getFlowExecution(flowExecutionId)
                .orElseThrow { ThainRuntimeException("flowExecution id does not exist：$flowExecutionId") }
        if (status == FlowExecutionStatus.RUNNING.code) {
            try {
                val (id, name, _, _, _, _, _, _, _, _, _, slaEmail, slaKill) = processEngine.processEngineStorage.flowDao.getFlow(flowId)
                        .orElseThrow { ThainRuntimeException("flow does not exist， flowId:$flowId") }
                if (slaKill) {
                    processEngine.thainFacade.killFlowExecution(flowId, flowExecutionId, true, "auto", "auto")
                }
                if (StringUtils.isNotBlank(slaEmail)) {
                    processEngine.processEngineStorage.mailService.send(
                            slaEmail.trim { it <= ' ' }.split(",".toRegex()).toTypedArray(),
                            "Thain SLA提醒",
                            "您的任务：$name($id), 超出期望的执行时间"
                    )
                }
            } catch (e: Exception) {
                log.error("kill failed, flowExecutionId:$flowExecutionId", e)
            }
        }
        context.scheduler.deleteJob(context.jobDetail.key)
    }

    companion object {
        private val SLA_JOB_MAP: MutableMap<String, SlaJob> = ConcurrentHashMap()

        @JvmStatic
        fun getInstance(processEngine: ProcessEngine): SlaJob {
            return SLA_JOB_MAP.computeIfAbsent(processEngine.processEngineId) { SlaJob(processEngine) }
        }
    }

}
