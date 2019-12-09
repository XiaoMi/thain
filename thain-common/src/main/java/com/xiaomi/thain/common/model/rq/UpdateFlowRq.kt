/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model.rq

import com.xiaomi.thain.common.model.dr.FlowDr

/**
 * add flow model
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
        val slaKill: Boolean = false
//    /**
//     * 最后一次运行状态,com.xiaomi.thain.common.constant.FlowLastRunStatus
//     */
//    val lastRunStatus: Int?,
//    /**
//     * 调度状态，1 调度中、2 暂停调度、（3 未设置调度{只运行一次的任务}）
//     */
//    val schedulingStatus: Int? = null
) {
    constructor(addFlowRq: AddFlowRq, id: Long) : this(
            id, addFlowRq.name, addFlowRq.cron,
            addFlowRq.modifyCallbackUrl,
            addFlowRq.pauseContinuousFailure,
            addFlowRq.emailContinuousFailure,
            addFlowRq.callbackUrl,
            addFlowRq.callbackEmail,
            addFlowRq.slaDuration,
            addFlowRq.slaEmail
    )

    constructor(flowDr: FlowDr) : this(flowDr.id,
            flowDr.name, flowDr.cron, flowDr.modifyCallbackUrl, flowDr.pauseContinuousFailure,
            flowDr.emailContinuousFailure,
            flowDr.callbackUrl, flowDr.callbackEmail, flowDr.slaDuration, flowDr.slaEmail)

}

