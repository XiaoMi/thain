package com.xiaomi.thain.core.process.runtime.executor.service

import com.xiaomi.thain.common.constant.FlowExecutionStatus
import com.xiaomi.thain.common.constant.FlowLastRunStatus
import com.xiaomi.thain.core.model.dr.FlowDr
import com.xiaomi.thain.core.process.ProcessEngine
import com.xiaomi.thain.core.process.ProcessEngineStorage
import com.xiaomi.thain.core.process.runtime.log.FlowExecutionLogHandler
import com.xiaomi.thain.core.process.runtime.notice.FlowHttpNotice

/**
 * Date 19-5-21 上午10:46
 * 任务服务类
 * http callback, mail callback, sla, retry
 *
 * @author liangyongrui@xiaomi.com
 */
class FlowExecutionService(private val flowExecutionId: Long,
                           private val flowDr: FlowDr,
                           private val retryNumber: Int,
                           private val processEngineStorage: ProcessEngineStorage) {

    private val flowExecutionLogHandler = FlowExecutionLogHandler.getInstance(flowExecutionId, processEngineStorage)
    private val mailNotice = processEngineStorage.getMailNotice(flowDr.callbackEmail)
    private val flowHttpNotice = FlowHttpNotice.getInstance(flowDr.callbackUrl, flowDr.id, flowExecutionId)
    private val flowService = FlowService.getInstance(flowDr.id, processEngineStorage)
    private val flowExecutionDao = processEngineStorage.flowExecutionDao
    /**
     * 如果是异常结束,异常信息.
     * 正常结束时，errorMessage为""
     */
    private var errorMessage = ""

    private var flowExecutionEndStatus = FlowExecutionStatus.SUCCESS

    /**
     * 开始任务
     */
    fun startFlowExecution() {
        if (retryNumber == 0) {
            flowService.startFlow()
            flowHttpNotice.sendStart()
        }
        if (flowDr.slaDuration > 0) {
            ProcessEngine.getInstance(processEngineStorage.processEngineId).thainFacade
                    .schedulerEngine
                    .addSla(flowExecutionId, flowDr)
        }
        flowExecutionLogHandler.addInfo("begin to execute flow：$flowExecutionId")
    }

    /**
     * 添加错误
     */
    fun addError(message: String) {
        errorMessage = message
        flowExecutionEndStatus = FlowExecutionStatus.ERROR
    }

    /**
     * 添加错误
     */
    fun autoKilled() {
        flowExecutionEndStatus = FlowExecutionStatus.AUTO_KILLED
    }

    /**
     * 添加错误
     */
    fun killed() {
        flowExecutionEndStatus = FlowExecutionStatus.KILLED
    }


    /**
     * 结束任务
     */
    fun endFlowExecution() {
        try {
            when (flowExecutionEndStatus) {
                FlowExecutionStatus.SUCCESS -> {
                    flowExecutionLogHandler.endSuccess()
                    flowHttpNotice.sendSuccess()
                    flowService.endFlow(FlowLastRunStatus.SUCCESS)
                }
                FlowExecutionStatus.KILLED -> {
                    errorMessage = "manual kill"
                    flowHttpNotice.sendKilled()
                    flowService.endFlow(FlowLastRunStatus.KILLED)
                }
                FlowExecutionStatus.AUTO_KILLED -> {
                    errorMessage = "auto kill"
                    flowHttpNotice.sendAutoKilled()
                    flowService.endFlow(FlowLastRunStatus.AUTO_KILLED)
                }
                else -> {
                    if (flowDr.retryNumber <= retryNumber) {
                        try {
                            flowHttpNotice.sendError(errorMessage)
                            mailNotice.sendError(errorMessage)
                            checkContinuousFailure()
                        } finally {
                            flowService.endFlow(FlowLastRunStatus.ERROR)
                        }
                    } else {
                        flowExecutionEndStatus = FlowExecutionStatus.ERROR_WAITING_RETRY
                        ProcessEngine.getInstance(processEngineStorage.processEngineId).thainFacade
                                .schedulerEngine
                                .addRetry(flowDr, retryNumber + 1)
                    }
                }
            }
            if (errorMessage.isNotBlank()) {
                flowExecutionLogHandler.endError(errorMessage)
            }
        } finally {
            processEngineStorage.flowExecutionDao.updateFlowExecutionStatus(flowExecutionId, flowExecutionEndStatus.code)
        }
    }

    fun addInfo(s: String) {
        flowExecutionLogHandler.addInfo(s)
    }

    /**
     * 连续失败暂停任务
     */
    private fun checkContinuousFailure() {
        flowDr.pauseContinuousFailure.takeIf { it > 0 }
                ?.let { flowExecutionDao.getLatest(flowDr.id, flowDr.pauseContinuousFailure) }
                ?.filter { FlowExecutionStatus.getInstance(it.status) == FlowExecutionStatus.ERROR }
                ?.takeIf { it.count() >= flowDr.pauseContinuousFailure - 1 }
                ?.apply {
                    ProcessEngine.getInstance(processEngineStorage.processEngineId).thainFacade.pauseFlow(
                            flowDr.id,
                            "auto",
                            "auto",
                            true)
                }
                ?.takeIf { !flowDr.emailContinuousFailure.isBlank() }
                ?.apply {
                    processEngineStorage.mailService.send(
                            flowDr.emailContinuousFailure.trim().split(",".toRegex()).toTypedArray(),
                            "Thain 任务连续失败通知",
                            "您的任务：[thain-${flowDr.id}]${flowDr.name}, 连续失败了${flowDr.pauseContinuousFailure}次，任务已经暂停。最近一次失败原因：$errorMessage"
                    )
                }
    }

}
