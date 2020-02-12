package com.xiaomi.thain.server.config

import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.server.model.ThainUser
import com.xiaomi.thain.server.service.UserService
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * @author miaoyu3@xiaomi.com
 * @date 19-8-7上午10:02
 */
@Component
class CustomOauth2UserServiceImpl(private val thainUserService: UserService) : OAuth2UserService<OidcUserRequest, OidcUser> {
    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        Assert.notNull(userRequest, "userRequest cannot be null")
        val authorities: Set<GrantedAuthority> = setOf(
                OidcUserAuthority(userRequest.idToken, null))
        val user: OidcUser
        val userNameAttributeName = userRequest.clientRegistration
                .providerDetails.userInfoEndpoint.userNameAttributeName
        user = if (StringUtils.hasText(userNameAttributeName)) {
            DefaultOidcUser(authorities, userRequest.idToken, null, userNameAttributeName)
        } else {
            DefaultOidcUser(authorities, userRequest.idToken, null as OidcUserInfo?)
        }
        //第三方登陆后插入数据库
        val userId = user.getAttributes()["email"].toString()
        return thainUserService.getUserById(userId) ?: run {
            val userName = user.getAttributes()["name"].toString()
            thainUserService.insertThirdUser(ThainUser(userId = userId, username = userName))
            thainUserService.getUserById(userId) ?: throw  ThainRuntimeException("get user error")
        }
    }

}
