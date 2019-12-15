package com.xiaomi.thain.core.scheduler.job

import com.xiaomi.thain.core.process.ProcessEngine
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.util.concurrent.ConcurrentHashMap

/**
 * @author liangyongrui
 */
class CleanJob private constructor(private val processEngine: ProcessEngine) : Job {

    override fun execute(context: JobExecutionContext) {
        processEngine.processEngineStorage.flowDao.cleanUpExpiredAndDeletedFlow()
        processEngine.processEngineStorage.jobDao.cleanUpExpiredAndDeletedJob()
        processEngine.processEngineStorage.flowExecutionDao.cleanUpExpiredFlowExecution()
        processEngine.processEngineStorage.jobExecutionDao.cleanUpExpiredFlowExecution()
    }

    companion object {
        private val CLEAN_JOB_MAP = ConcurrentHashMap<String, CleanJob>()
        @JvmStatic
        fun getInstance(processEngine: ProcessEngine): CleanJob {
            return CLEAN_JOB_MAP.computeIfAbsent(processEngine.processEngineId) { CleanJob(processEngine) }
        }
    }

}