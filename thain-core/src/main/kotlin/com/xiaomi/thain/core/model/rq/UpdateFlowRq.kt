package com.xiaomi.thain.core.model.rq

import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.core.model.dr.FlowDr

/**
 * update flow model
 *
 * @author liangyongrui@xiaomi.com
 */
data class UpdateFlowRq(
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
        val retryNumber: Int?,
        val retryTimeInterval: Int?,

        /**
         * 用于日志记录
         */
        val appId: String,
        /**
         * 用于日志记录
         */
        val username: String
) {

    constructor(addFlowRq: AddFlowRq, id: Long) : this(
            id,
            addFlowRq.name,
            addFlowRq.cron,
            addFlowRq.modifyCallbackUrl,
            addFlowRq.pauseContinuousFailure,
            addFlowRq.emailContinuousFailure,
            addFlowRq.callbackUrl,
            addFlowRq.callbackEmail,
            addFlowRq.slaDuration,
            addFlowRq.slaEmail,
            addFlowRq.slaKill,
            addFlowRq.retryNumber,
            addFlowRq.retryTimeInterval,
            addFlowRq.createAppId ?: throw ThainException("app id can not empty"),
            addFlowRq.createUser ?: throw ThainException("create user can not empty")
    )

    constructor(flowDr: FlowDr) : this(
            flowDr.id,
            flowDr.name,
            flowDr.cron,
            flowDr.modifyCallbackUrl,
            flowDr.pauseContinuousFailure,
            flowDr.emailContinuousFailure,
            flowDr.callbackUrl,
            flowDr.callbackEmail,
            flowDr.slaDuration,
            flowDr.slaEmail,
            flowDr.slaKill,
            flowDr.retryNumber,
            flowDr.retryTimeInterval,
            flowDr.createAppId,
            flowDr.createUser
    )

}
