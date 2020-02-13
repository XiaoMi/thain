package com.xiaomi.thain.server.config

import com.xiaomi.thain.server.model.ThainUser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.ldap.core.DirContextOperations
import org.springframework.ldap.core.support.LdapContextSource
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.ldap.authentication.BindAuthenticator
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper

/**
 * @author liangyongrui
 */
@Lazy
@Configuration
class LdapAuthenticationConfig(private val ldapContextSource: LdapContextSource) {
    @Bean("LdapAuthentication")
    fun ldapAuthenticationProvider(): AuthenticationProvider {
        val bindAuthenticator = BindAuthenticator(ldapContextSource)
        bindAuthenticator.setUserDnPatterns(arrayOf("uid={0},ou=people"))
        val authProvider = LdapAuthenticationProvider(bindAuthenticator)
        authProvider.setUserDetailsContextMapper(object : LdapUserDetailsMapper() {
            override fun mapUserFromContext(ctx: DirContextOperations, username: String,
                                            authorities: Collection<GrantedAuthority>): UserDetails {
                var p = ctx.getObjectAttribute(UP_ATTRIBUTE_NAME)
                if (p == null) {
                    p = "protected"
                }
                if (p !is String) {
                    p = String((p as ByteArray))
                }
                return ThainUser(userId = ctx.getStringAttribute(USER_ID_ATTRIBUTE_NAME),
                        username = ctx.getStringAttribute(USERNAME_ATTRIBUTE_NAME),
                        passwordHash = p.toString(),
                        email = null,
                        admin = "admin" == ctx.getStringAttribute(GROUP_ATTRIBUTE_NAME),
                        appIds = setOf())
            }
        })
        return authProvider
    }

    companion object {
        private const val UP_ATTRIBUTE_NAME = "userPassword"
        private const val USERNAME_ATTRIBUTE_NAME = "sn"
        private const val USER_ID_ATTRIBUTE_NAME = "uid"
        private const val GROUP_ATTRIBUTE_NAME = "ou"
    }

}
