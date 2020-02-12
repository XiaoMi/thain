package com.xiaomi.thain.server.model.rp

/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/24
 */
data class X5ConfigRp(
        val appId: String,
        val appName: String,
        val appKey: String,
        val principals: List<String>,
        val description: String?,
        val createTime: Long
)
