package com.xiaomi.thain.core

import com.xiaomi.thain.common.constant.FlowExecutionStatus
import com.xiaomi.thain.common.constant.FlowSchedulingStatus
import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException
import com.xiaomi.thain.common.model.dp.UpdateFlowDp
import com.xiaomi.thain.common.model.rq.AddFlowAndJobsRq
import com.xiaomi.thain.common.model.rq.AddJobRq
import com.xiaomi.thain.common.model.rq.UpdateFlowRq
import com.xiaomi.thain.common.utils.ifNull
import com.xiaomi.thain.core.process.ProcessEngine
import com.xiaomi.thain.core.process.ProcessEngineConfiguration
import com.xiaomi.thain.core.scheduler.SchedulerEngine
import com.xiaomi.thain.core.scheduler.SchedulerEngineConfiguration
import com.xiaomi.thain.core.utils.SendModifyUtils
import org.apache.commons.lang3.StringUtils.isNotBlank
import org.quartz.CronExpression
import org.quartz.SchedulerException
import org.slf4j.LoggerFactory
import java.io.IOException
import java.text.MessageFormat
import java.text.ParseException

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-16 下午8:38
 */
class ThainFacade private constructor(processEngineConfiguration: ProcessEngineConfiguration,
                                      schedulerEngineConfiguration: SchedulerEngineConfiguration) {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    val schedulerEngine: SchedulerEngine

    private val processEngine: ProcessEngine = ProcessEngine.newInstance(processEngineConfiguration, this)

    init {
        schedulerEngine = SchedulerEngine.getInstance(schedulerEngineConfiguration, processEngine)
        schedulerEngine.start()
    }

    /**
     * 新建任务, cron为空的则只部署，不调度, 这个flowJson是不含id的，如果含id也没用
     */
    @Throws(ThainException::class)
    fun addFlow(addRq: AddFlowAndJobsRq): Long {
        val flowId = processEngine.addFlow(addRq.flowModel, addRq.jobModelList)
                .orElseThrow { ThainException("failed to insert flow") }
        if (addRq.flowModel.cron.isNullOrBlank()) {
            return flowId
        }
        try {
            CronExpression.validateExpression(addRq.flowModel.cron)
            schedulerEngine.addFlow(flowId, addRq.flowModel.cron!!)
        } catch (e: Exception) {
            processEngine.deleteFlow(flowId)
            throw ThainException(e)
        }
        return flowId
    }

    /**
     * 更新flow
     */
    @Throws(SchedulerException::class, ThainException::class, ParseException::class)
    fun updateFlow(updateFlowRq: UpdateFlowRq, jobModelList: List<AddJobRq>) {
        val schedulingStatus = updateFlowRq.cron
                .takeIf { !it.isNullOrBlank() }
                ?.let {
                    CronExpression.validateExpression(it)
                    schedulerEngine.addFlow(updateFlowRq.id, it)
                    FlowSchedulingStatus.getInstance(processEngine.getFlow(updateFlowRq.id).schedulingStatus)
                            .takeIf { status -> status != FlowSchedulingStatus.NOT_SET }
                            .ifNull { FlowSchedulingStatus.SCHEDULING }
                }
                .ifNull { FlowSchedulingStatus.NOT_SET }
        val updateFlowDp = UpdateFlowDp(updateFlowRq, schedulingStatus)
        processEngine.processEngineStorage.flowDao.updateFlow(updateFlowDp, jobModelList)
    }

    /**
     * 删除Flow
     */
    @Throws(SchedulerException::class)
    fun deleteFlow(flowId: Long) {
        schedulerEngine.deleteFlow(flowId)
        processEngine.deleteFlow(flowId)
    }

    /**
     * 触发某个Flow
     */
    @Throws(ThainException::class, ThainRepeatExecutionException::class)
    fun startFlow(flowId: Long): Long {
        return processEngine.startProcess(flowId)
    }

    val componentDefineJsonList: Map<String, String>
        get() = processEngine.processEngineStorage.componentService.componentDefineJsonList

    @Throws(ThainException::class)
    fun pauseFlow(flowId: Long) {
        val flowDr = processEngine.processEngineStorage.flowDao.getFlow(flowId)
                .orElseThrow { ThainException(MessageFormat.format(NON_EXIST_FLOW, flowId)) }
        try {
            processEngine.processEngineStorage.flowDao.pauseFlow(flowId)
            schedulerEngine.deleteFlow(flowId)
            if (isNotBlank(flowDr.modifyCallbackUrl)) {
                SendModifyUtils.sendPause(flowId, flowDr.modifyCallbackUrl)
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
            throw ThainException(e)
        }
    }

    @Throws(ThainException::class, SchedulerException::class, java.io.IOException::class)
    fun schedulingFlow(flowId: Long) {
        val flowModel = processEngine.processEngineStorage.flowDao.getFlow(flowId)
                .orElseThrow { ThainException(MessageFormat.format(NON_EXIST_FLOW, flowId)) }
        schedulerEngine.addFlow(flowModel.id, flowModel.cron)
        processEngine.processEngineStorage.flowDao
                .updateSchedulingStatus(flowModel.id, FlowSchedulingStatus.SCHEDULING)
        if (isNotBlank(flowModel.modifyCallbackUrl)) {
            SendModifyUtils.sendScheduling(flowId, flowModel.modifyCallbackUrl)
        }
    }

    @Throws(ThainException::class)
    fun killFlowExecution(flowExecutionId: Long, auto: Boolean) {
        val flowExecutionModel = processEngine.processEngineStorage.flowExecutionDao
                .getFlowExecution(flowExecutionId)
                .orElseThrow { ThainException("flowExecution id does not exist：$flowExecutionId") }
        if (FlowExecutionStatus.getInstance(flowExecutionModel.status) != FlowExecutionStatus.RUNNING) {
            throw ThainException("flowExecution does not running: $flowExecutionId")
        }
        if (auto) {
            processEngine.processEngineStorage.flowExecutionDao.updateFlowExecutionStatus(flowExecutionId, FlowExecutionStatus.AUTO_KILLED.code)
        } else {
            processEngine.processEngineStorage.flowExecutionDao.updateFlowExecutionStatus(flowExecutionId, FlowExecutionStatus.KILLED.code)
        }
        processEngine.processEngineStorage.jobExecutionDao.killJobExecution(flowExecutionId)
        processEngine.processEngineStorage.flowDao.killFlow(flowExecutionModel.flowId)
    }

    @Throws(ThainException::class, ParseException::class, SchedulerException::class, IOException::class)
    fun updateCron(flowId: Long, cron: String?) {
        val flowDr = processEngine.processEngineStorage.flowDao.getFlow(flowId)
                .orElseThrow { ThainException(MessageFormat.format(NON_EXIST_FLOW, flowId)) }
        val jobModelList = processEngine.processEngineStorage.jobDao.getJobs(flowId).map { AddJobRq(it) }
        if (cron == null) {
            updateFlow(UpdateFlowRq(flowDr), jobModelList)
        } else {
            updateFlow(UpdateFlowRq(flowDr.copy(cron = cron)), jobModelList)
        }
        if (isNotBlank(flowDr.modifyCallbackUrl)) {
            SendModifyUtils.sendScheduling(flowId, flowDr.modifyCallbackUrl)
        }
    }

    companion object {
        private const val NON_EXIST_FLOW = "flow does not exist:{0}"
        @JvmStatic
        fun getInstance(processEngineConfiguration: ProcessEngineConfiguration,
                        schedulerEngineConfiguration: SchedulerEngineConfiguration): ThainFacade {
            return ThainFacade(processEngineConfiguration, schedulerEngineConfiguration)
        }
    }

}
