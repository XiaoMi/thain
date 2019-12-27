package com.xiaomi.thain.core.model.dp

class AddFlowOperationLogDp(
        val flowId: Long,
        val operationType: Int,
        val appId: String,
        val username: String,
        val extraInfo: String
) {
    val id: Long? = null
}
