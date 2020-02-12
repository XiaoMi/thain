package com.xiaomi.thain.core.entity

/**
 * Date 2019/8/9 下午3:42
 *
 * @author liangyongrui@xiaomi.com
 */
class ThainUser(
        val id: Long,
        val userId: String?,
        val userName: String?,
        val passwordHash: String?,
        val email: String?,
        val admin: Boolean
)
