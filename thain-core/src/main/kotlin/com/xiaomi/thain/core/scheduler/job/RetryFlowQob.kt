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
class RetryFlowQob private constructor(private val processEngine: ProcessEngine) : Job {
    override fun execute(context: JobExecutionContext) {
        val dataMap = context.jobDetail.jobDataMap
        val flowId = dataMap.getLong("flowId")
        val retryNumber = dataMap.getInt("retryNumber")
        //todo 触发重试
    }

    companion object {
        private val RETRY_FLOW_QOB_MAP: MutableMap<String, RetryFlowQob> = ConcurrentHashMap()
        fun getInstance(processEngine: ProcessEngine): RetryFlowQob {
            return RETRY_FLOW_QOB_MAP.computeIfAbsent(processEngine.processEngineId) { RetryFlowQob(processEngine) }
        }
    }

}
