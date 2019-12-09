/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.model.dp

import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.model.rq.kt.AddFlowRq

/**
 * add flow model
 *
 * @author liangyongrui@xiaomi.com
 */
data class AddFlowDp(
        /**
         * 添加成功后 id存在
         */
        val id: Long?,
        val name: String,
        val cron: String?,
        val modifyCallbackUrl: String?,
        /**
         * 0 则不暂停
         */
        val pauseContinuousFailure: Long?,
        val emailContinuousFailure: String?,
        val createUser: String,
        val callbackUrl: String?,
        val callbackEmail: String?,
        /**
         * 创建的appId,"thain" 为网页创建
         */
        val createAppId: String?,
        /**
         * 秒时间戳
         */
        val slaDuration: Long?,
        val slaEmail: String?,
        val slaKill: Boolean = false,
        val retryNumbers: Int?,
        val timeInterval: Int?,
        val schedulingStatus: Int
) {
    constructor(addFlowRq: AddFlowRq, flowSchedulingStatus: Int) : this(
            null,
            addFlowRq.name,
            addFlowRq.cron,
            addFlowRq.modifyCallbackUrl,
            addFlowRq.pauseContinuousFailure,
            addFlowRq.emailContinuousFailure,
            addFlowRq.createUser ?: throw ThainException("No set create user"),
            addFlowRq.callbackUrl,
            addFlowRq.callbackEmail,
            addFlowRq.createAppId ?: throw ThainException("No set create app id"),
            addFlowRq.slaDuration,
            addFlowRq.slaEmail,
            addFlowRq.slaKill,
            addFlowRq.retryNumbers,
            addFlowRq.timeInterval,
            flowSchedulingStatus)
}
