package com.xiaomi.thain.server.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.OidcUser

class ThainUser(
        val userId: String,
        private val username: String,
        private val passwordHash: String?,
        val admin: Boolean,
        private val appIds: Set<String>?,
        private val email: String?
) : UserDetails, OidcUser {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val result = HashSet<GrantedAuthority>()
        if (admin) {
            result.add(GrantedAuthority { "admin" })
        }
        appIds?.map { SimpleGrantedAuthority(it) }?.forEach { result.add(it) }
        return result
    }

    override fun getPassword(): String? {
        return passwordHash
    }

    override fun getAttributes(): Map<String, Any>? {
        return null
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String {
        return username
    }

    override fun getEmail(): String? {
        return email
    }

    override fun getName(): String? {
        return null
    }

    override fun getClaims(): Map<String, Any>? {
        return null
    }

    override fun getUserInfo(): OidcUserInfo? {
        return null
    }

    override fun getIdToken(): OidcIdToken? {
        return null
    }
}
