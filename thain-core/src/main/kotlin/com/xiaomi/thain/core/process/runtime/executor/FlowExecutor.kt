package com.xiaomi.thain.core.process.runtime.executor

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.mchange.lang.ThrowableUtils
import com.xiaomi.thain.common.constant.FlowExecutionStatus
import com.xiaomi.thain.common.constant.JobExecutionStatus
import com.xiaomi.thain.common.exception.ThainCreateFlowExecutionException
import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.common.model.JobExecutionModel
import com.xiaomi.thain.common.model.dr.FlowExecutionDr
import com.xiaomi.thain.common.utils.copyOf
import com.xiaomi.thain.core.constant.FlowExecutionTriggerType
import com.xiaomi.thain.core.model.dr.JobDr
import com.xiaomi.thain.core.process.ProcessEngineStorage
import com.xiaomi.thain.core.process.runtime.checker.JobConditionChecker
import com.xiaomi.thain.core.process.runtime.executor.service.FlowExecutionService
import com.xiaomi.thain.core.process.runtime.storage.FlowExecutionStorage
import com.xiaomi.thain.core.process.runtime.storage.GLOBAL_JOB_NAME
import com.xiaomi.thain.core.thread.pool.ThainThreadPool
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 任务执行器: 创建执行任务，管理执行流程
 *
 * @author liangyongrui@xiaomi.com
 */
class FlowExecutor(flowExecutionDr: FlowExecutionDr,
                   private val processEngineStorage: ProcessEngineStorage,
                   retryNumber: Int) {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    private val flowDr = processEngineStorage.flowDao.getFlow(flowExecutionDr.flowId) ?: throw ThainException()

    /**
     * 监控节点是否执行完
     */
    private val jobFutureQueue = ConcurrentLinkedQueue<CompletableFuture<Void>>()

    private val flowExecutionId: Long
    private val jobConditionChecker: JobConditionChecker
    private val flowExecutionStorage: FlowExecutionStorage
    private val flowExecutionService: FlowExecutionService
    private val flowExecutionJobThreadPool: ThainThreadPool
    private val jobExecutionModelMap: Map<Long, JobExecutionModel>

    /**
     * 当前未执行的节点
     */
    private var notExecutedJobsPool: Collection<JobDr>


    /**
     * 流程执行入口
     */
    fun start() {
        try {
            flowExecutionService.startFlowExecution()
            runExecutableJobs()
            while (!jobFutureQueue.isEmpty()) {
                jobFutureQueue.poll().join()
            }
        } catch (e: Exception) {
            log.error("", e)
            flowExecutionService.addError(ExceptionUtils.getStackTrace(e))
        } finally {
            try {
                flowExecutionService.endFlowExecution()
            } finally {
                flowExecutionJobThreadPool.shutdown()
                FlowExecutionStorage.drop(flowExecutionId)
            }
        }
    }

    /**
     * 执行可以执行的节点
     */
    @Synchronized
    private fun runExecutableJobs() {
        val flowExecutionModel = processEngineStorage.flowExecutionDao.getFlowExecution(flowExecutionId)
                ?: throw ThainRuntimeException("Failed to read FlowExecution information, flowExecutionId: $flowExecutionId")
        when (FlowExecutionStatus.getInstance(flowExecutionModel.status)) {
            FlowExecutionStatus.KILLED -> {
                flowExecutionService.killed()
                return
            }
            FlowExecutionStatus.AUTO_KILLED -> {
                flowExecutionService.autoKilled()
                return
            }
            else -> {
                // do down
            }
        }
        executableJobs.forEach { job ->
            val future = CompletableFuture.runAsync(Runnable {
                flowExecutionService.addInfo("Start executing the job [${job.name}]")
                try {
                    JobExecutor.start(flowExecutionId, job, jobExecutionModelMap[job.id]
                            ?: error(""), processEngineStorage)
                } catch (e: Exception) {
                    flowExecutionService.addError("Job[${job.name}] exception: ${ExceptionUtils.getRootCauseMessage(e)}")
                    return@Runnable
                } catch (e: Throwable) {
                    processEngineStorage.mailService.sendSeriousError(ThrowableUtils.extractStackTrace(e))
                    flowExecutionService.addError("Job[${job.name}] exception: ${e.message}")
                    return@Runnable
                }
                flowExecutionService.addInfo("Execute job[${job.name}] complete")
                flowExecutionStorage.addFinishJob(job.name)
                runExecutableJobs()
            }, flowExecutionJobThreadPool)
            jobFutureQueue.add(future)
        }
    }

    private val executableJobs: Collection<JobDr>
        get() = notExecutedJobsPool
                .filter { jobConditionChecker.executable(it.condition) }
                .toSet()
                .also { jobs ->
                    notExecutedJobsPool = notExecutedJobsPool
                            .filter { !jobs.contains(it) }
                            .toList()
                }

    /**
     * 获取FlowExecutionExecutor实例
     * effect：为了获取flowExecutionId 会在数据库中创建一个flowExecution
     */
    init {
        try {
            processEngineStorage.flowExecutionDao
                    .updateFlowExecutionStatus(flowExecutionDr.id, FlowExecutionStatus.RUNNING.code)
            flowExecutionId = flowExecutionDr.id
            val jobModelList = processEngineStorage.jobDao.getJobs(flowDr.id)
            flowExecutionService = FlowExecutionService(flowExecutionId, flowDr, retryNumber, processEngineStorage)
            notExecutedJobsPool = jobModelList.copyOf()
            jobConditionChecker = JobConditionChecker.getInstance(flowExecutionId)
            flowExecutionStorage = FlowExecutionStorage.getInstance(flowExecutionId)
            flowExecutionDr.variables
                    ?.let { JSON.parseObject(it, object : TypeReference<Map<String, String>>() {}) }
                    ?.entries?.forEach { flowExecutionStorage.put(GLOBAL_JOB_NAME, it.key, it.value) }
            flowExecutionJobThreadPool = processEngineStorage.flowExecutionJobThreadPool(flowExecutionId)
            jobExecutionModelMap = jobModelList
                    .map {
                        it.id to JobExecutionModel.builder()
                                .jobId(it.id)
                                .flowExecutionId(flowExecutionId)
                                .status(JobExecutionStatus.NEVER.code).build()
                    }
                    .onEach { processEngineStorage.jobExecutionDao.add(it.second) }
                    .toMap()

            log.info("begin start flow: {}, flowExecutionId: {}, Trigger: {}",
                    flowExecutionDr.flowId,
                    flowExecutionDr.id,
                    FlowExecutionTriggerType.getInstance(flowExecutionDr.triggerType))
        } catch (e: Exception) {
            log.error("", e)
            throw ThainCreateFlowExecutionException(flowDr.id, e.message)
        }
    }
}
