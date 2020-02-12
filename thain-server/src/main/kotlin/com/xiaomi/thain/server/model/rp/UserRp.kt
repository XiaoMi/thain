package com.xiaomi.thain.server.model.rp

data class UserRp(
        val userId: String,
        val admin: Boolean,
        val email: String?,
        val username: String
)
