package com.xiaomi.thain.common.model.dp

import com.xiaomi.thain.common.constant.FlowSchedulingStatus
import com.xiaomi.thain.common.model.rq.UpdateFlowRq

/**
 * add flow model
 *
 * @author liangyongrui@xiaomi.com
 */
data class UpdateFlowDp(
        val id: Long,
        val name: String?,
        val cron: String?,
        val modifyCallbackUrl: String?,
        val pauseContinuousFailure: Long?,
        val emailContinuousFailure: String?,
        val callbackUrl: String?,
        val callbackEmail: String?,
        /**
         * 秒时间戳
         */
        val slaDuration: Long?,
        val slaEmail: String?,
        val slaKill: Boolean = false,
        /**
         * 调度状态，1 调度中、2 暂停调度、（3 未设置调度{只运行一次的任务}）
         */
        val schedulingStatus: Int,
        val retryNumber: Int?,
        val timeInterval: Int?
) {
    constructor(updateFlowRq: UpdateFlowRq, schedulingStatus: FlowSchedulingStatus) : this(
            updateFlowRq.id,
            updateFlowRq.name,
            updateFlowRq.cron,
            updateFlowRq.modifyCallbackUrl,
            updateFlowRq.pauseContinuousFailure,
            updateFlowRq.emailContinuousFailure,
            updateFlowRq.callbackUrl,
            updateFlowRq.callbackEmail,
            updateFlowRq.slaDuration,
            updateFlowRq.slaEmail,
            updateFlowRq.slaKill,
            schedulingStatus.code,
            updateFlowRq.retryNumber,
            updateFlowRq.timeInterval
    )

}
