package com.xiaomi.thain.core

import kotlin.collections.map
import kotlin.jvm.javaClass

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-16 下午8:38
 */
class ThainFacade private constructor(processEngineConfiguration: com.xiaomi.thain.core.process.ProcessEngineConfiguration,
                                      schedulerEngineConfiguration: com.xiaomi.thain.core.scheduler.SchedulerEngineConfiguration) {

    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)!!

    val schedulerEngine: com.xiaomi.thain.core.scheduler.SchedulerEngine

    private val processEngine: com.xiaomi.thain.core.process.ProcessEngine = com.xiaomi.thain.core.process.ProcessEngine.newInstance(processEngineConfiguration, this)

    init {
        schedulerEngine = com.xiaomi.thain.core.scheduler.SchedulerEngine.getInstance(schedulerEngineConfiguration, processEngine)
        schedulerEngine.start()
    }

    /**
     * 新建任务, cron为空的则只部署，不调度, 这个flowJson是不含id的，如果含id也没用
     */
    @Throws(com.xiaomi.thain.common.exception.ThainException::class)
    fun addFlow(addRq: AddFlowAndJobsRq): Long {
        val flowId = processEngine.addFlow(addRq.flowModel, addRq.jobModelList)
                .orElseThrow { com.xiaomi.thain.common.exception.ThainException("failed to insert flow") }
        if (org.apache.commons.lang3.StringUtils.isBlank(addRq.flowModel.cron)) {
            return flowId
        }
        try {
            org.quartz.CronExpression.validateExpression(addRq.flowModel.cron)
            schedulerEngine.addFlow(flowId, addRq.flowModel.cron!!)
        } catch (e: Exception) {
            processEngine.deleteFlow(flowId)
            throw com.xiaomi.thain.common.exception.ThainException(e)
        }
        return flowId
    }

    /**
     * 更新flow
     */
    @Throws(org.quartz.SchedulerException::class, com.xiaomi.thain.common.exception.ThainException::class, java.text.ParseException::class)
    fun updateFlow(updateFlowRq: UpdateFlowRq, jobModelList: List<AddJobRq>) {
        val schedulingStatus = if (!updateFlowRq.cron.isNullOrBlank()) {
            org.quartz.CronExpression.validateExpression(updateFlowRq.cron)
            schedulerEngine.addFlow(updateFlowRq.id, updateFlowRq.cron!!)
            processEngine.getFlow(updateFlowRq.id).schedulingStatus
        } else {
            1
        }
        val updateFlowDp = UpdateFlowDp(updateFlowRq, schedulingStatus)
        processEngine.processEngineStorage.flowDao.updateFlow(updateFlowDp, jobModelList)
    }

    /**
     * 删除Flow
     */
    @Throws(org.quartz.SchedulerException::class)
    fun deleteFlow(flowId: Long) {
        schedulerEngine.deleteFlow(flowId)
        processEngine.deleteFlow(flowId)
    }

    /**
     * 触发某个Flow
     */
    @Throws(com.xiaomi.thain.common.exception.ThainException::class, com.xiaomi.thain.common.exception.ThainRepeatExecutionException::class)
    fun startFlow(flowId: Long): Long {
        return processEngine.startProcess(flowId)
    }

    val componentDefineJsonList: Map<String, String>
        get() = processEngine.processEngineStorage.componentService.componentDefineJsonList

    @Throws(com.xiaomi.thain.common.exception.ThainException::class)
    fun pauseFlow(flowId: Long) {
        val flowDr = processEngine.processEngineStorage.flowDao.getFlow(flowId)
                .orElseThrow { com.xiaomi.thain.common.exception.ThainException(java.text.MessageFormat.format(NON_EXIST_FLOW, flowId)) }
        try {
            processEngine.processEngineStorage.flowDao.pauseFlow(flowId)
            schedulerEngine.deleteFlow(flowId)
            if (org.apache.commons.lang3.StringUtils.isNotBlank(flowDr.modifyCallbackUrl)) {
                com.xiaomi.thain.core.utils.SendModifyUtils.sendPause(flowId, flowDr.modifyCallbackUrl)
            }
        } catch (e: Exception) {
            log.error("", e)
            try {
                val jobModelList = processEngine.processEngineStorage
                        .jobDao.getJobs(flowId)
                        .map { AddJobRq(it) }
                updateFlow(UpdateFlowRq(flowDr), jobModelList)
            } catch (ex: Exception) {
                log.error("", ex)
            }
            throw com.xiaomi.thain.common.exception.ThainException(e)
        }
    }

    @Throws(com.xiaomi.thain.common.exception.ThainException::class, org.quartz.SchedulerException::class, java.io.IOException::class)
    fun schedulingFlow(flowId: Long) {
        val flowModel = processEngine.processEngineStorage.flowDao.getFlow(flowId)
                .orElseThrow { com.xiaomi.thain.common.exception.ThainException(java.text.MessageFormat.format(NON_EXIST_FLOW, flowId)) }
        schedulerEngine.addFlow(flowModel.id, flowModel.cron)
        processEngine.processEngineStorage.flowDao
                .updateSchedulingStatus(flowModel.id, com.xiaomi.thain.common.constant.FlowSchedulingStatus.SCHEDULING)
        if (org.apache.commons.lang3.StringUtils.isNotBlank(flowModel.modifyCallbackUrl)) {
            com.xiaomi.thain.core.utils.SendModifyUtils.sendScheduling(flowId, flowModel.modifyCallbackUrl)
        }
    }

    @Throws(com.xiaomi.thain.common.exception.ThainException::class)
    fun killFlowExecution(flowExecutionId: Long, auto: Boolean) {
        val flowExecutionModel = processEngine.processEngineStorage.flowExecutionDao
                .getFlowExecution(flowExecutionId)
                .orElseThrow { com.xiaomi.thain.common.exception.ThainException("flowExecution id does not exist：$flowExecutionId") }
        if (com.xiaomi.thain.common.constant.FlowExecutionStatus.getInstance(flowExecutionModel.status) != com.xiaomi.thain.common.constant.FlowExecutionStatus.RUNNING) {
            throw com.xiaomi.thain.common.exception.ThainException("flowExecution does not running: $flowExecutionId")
        }
        if (auto) {
            processEngine.processEngineStorage.flowExecutionDao.updateFlowExecutionStatus(flowExecutionId, com.xiaomi.thain.common.constant.FlowExecutionStatus.AUTO_KILLED.code)
        } else {
            processEngine.processEngineStorage.flowExecutionDao.updateFlowExecutionStatus(flowExecutionId, com.xiaomi.thain.common.constant.FlowExecutionStatus.KILLED.code)
        }
        processEngine.processEngineStorage.jobExecutionDao.killJobExecution(flowExecutionId)
        processEngine.processEngineStorage.flowDao.killFlow(flowExecutionModel.flowId)
    }

    @Throws(com.xiaomi.thain.common.exception.ThainException::class, java.text.ParseException::class, org.quartz.SchedulerException::class, java.io.IOException::class)
    fun updateCron(flowId: Long, cron: String?) {
        val flowDr = processEngine.processEngineStorage.flowDao.getFlow(flowId)
                .orElseThrow { com.xiaomi.thain.common.exception.ThainException(java.text.MessageFormat.format(NON_EXIST_FLOW, flowId)) }
        val jobModelList = processEngine.processEngineStorage.jobDao.getJobs(flowId).map { AddJobRq(it) }
        if (cron == null) {
            updateFlow(UpdateFlowRq(flowDr), jobModelList)
        } else {
            updateFlow(UpdateFlowRq(flowDr.copy(cron = cron)), jobModelList)
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(flowDr.modifyCallbackUrl)) {
            com.xiaomi.thain.core.utils.SendModifyUtils.sendScheduling(flowId, flowDr.modifyCallbackUrl)
        }
    }

    companion object {
        private const val NON_EXIST_FLOW = "flow does not exist:{0}"
        @JvmStatic
        @Throws(com.xiaomi.thain.common.exception.scheduler.ThainSchedulerException::class, com.xiaomi.thain.common.exception.ThainMissRequiredArgumentsException::class, java.io.IOException::class, java.sql.SQLException::class, InterruptedException::class)
        fun getInstance(processEngineConfiguration: com.xiaomi.thain.core.process.ProcessEngineConfiguration,
                        schedulerEngineConfiguration: com.xiaomi.thain.core.scheduler.SchedulerEngineConfiguration): ThainFacade {
            return ThainFacade(processEngineConfiguration, schedulerEngineConfiguration)
        }
    }

}