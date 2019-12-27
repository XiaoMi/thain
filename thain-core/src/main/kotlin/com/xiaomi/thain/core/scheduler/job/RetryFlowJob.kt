package com.xiaomi.thain.core.scheduler.job

import com.xiaomi.thain.core.process.ProcessEngine
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.util.concurrent.ConcurrentHashMap

/**
 * Date 19-7-16 上午10:59
 *
 * @author liangyongrui@xiaomi.com
 */
class RetryFlowJob private constructor(private val processEngine: ProcessEngine) : Job {

    override fun execute(context: JobExecutionContext) {
        val dataMap = context.jobDetail.jobDataMap
        val flowId = dataMap.getLong("flowId")
        val retryNumber = dataMap.getInt("retryNumber")
        processEngine.retryFlow(flowId, retryNumber)
        context.scheduler.deleteJob(context.jobDetail.key)
    }

    companion object {
        private val RETRY_FLOW_JOB_MAP = ConcurrentHashMap<String, RetryFlowJob> ()
        @JvmStatic
        fun getInstance(processEngine: ProcessEngine): RetryFlowJob {
            return RETRY_FLOW_JOB_MAP.computeIfAbsent(processEngine.processEngineId) { RetryFlowJob(processEngine) }
        }
    }
}
