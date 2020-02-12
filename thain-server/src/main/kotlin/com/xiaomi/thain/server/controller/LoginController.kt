package com.xiaomi.thain.server.controller

import com.xiaomi.thain.common.entity.ApiResult
import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.server.handler.ThreadLocalUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author liangyongrui
 */
@RestController
@RequestMapping("api/login")
class LoginController {
    @GetMapping("current-user")
    fun currentUser(): ApiResult {
        return try {
            val user = ThreadLocalUser.thainUser
                    ?: throw ThainException("user does not exist")
            ApiResult.success(mapOf(
                    "userId" to user.userId,
                    "name" to user.username,
                    "authority" to user.authorities.map { it.authority }
            ))
        } catch (e: Exception) {
            ApiResult.fail(e.message)
        }
    }

    @GetMapping("logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ApiResult {
        return try {
            val auth = SecurityContextHolder.getContext().authentication
            if (auth != null) {
                SecurityContextLogoutHandler().logout(request, response, auth)
            }
            ApiResult.success()
        } catch (e: Exception) {
            ApiResult.fail("logout failed")
        }
    }

    @GetMapping("/csrf")
    fun csrf(token: CsrfToken): CsrfToken {
        return token
    }
}
