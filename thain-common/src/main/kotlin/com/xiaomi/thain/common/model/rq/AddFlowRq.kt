package com.xiaomi.thain.common.model.rq

/**
 * add flow model
 *
 * @author liangyongrui@xiaomi.com
 */
data class AddFlowRq(
        val id: Long?,
        /**
         * 添加成功后 id存在
         */
        val name: String,
        val cron: String?,
        val modifyCallbackUrl: String?,
        /**
         * 0 则不暂停
         */
        val pauseContinuousFailure: Long?,
        val emailContinuousFailure: String?,
        val createUser: String?,
        val callbackUrl: String?,
        val callbackEmail: String?,
        /**
         * 创建的appId,"thain" 为网页创建
         */
        val createAppId: String?,
        /**
         * 秒时间戳
         */
        val slaDuration: Long = 0,
        val slaEmail: String?,
        val slaKill: Boolean = false,
        val retryNumber: Int,
        val retryTimeInterval: Int
)
