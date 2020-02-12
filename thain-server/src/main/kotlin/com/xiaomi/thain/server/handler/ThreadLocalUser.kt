package com.xiaomi.thain.server.handler

import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.server.model.ThainUser
import org.springframework.security.core.context.SecurityContextHolder

/**
 * @author liangyongrui@xiaomi.com
 * @date 11/8/18 3:47 PM
 */
object ThreadLocalUser {
    val thainUser: ThainUser?
        get() = SecurityContextHolder.getContext().authentication
                ?.let { it.principal as ThainUser }

    /**
     * 获取当前用户id
     */
    val username: String
        get() = thainUser?.userId ?: throw ThainException("Failed to obtain logged-in user")

    val isAdmin: Boolean
        get() = thainUser?.admin ?: false

    val authorities: Set<String>
        get() = thainUser?.appIds ?: emptySet()
}
