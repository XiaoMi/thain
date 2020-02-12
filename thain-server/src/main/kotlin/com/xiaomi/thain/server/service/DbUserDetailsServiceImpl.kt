package com.xiaomi.thain.server.service

import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.server.dao.UserDao
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

/**
 * @author miaoyu
 */
@Service("DbUserDetailsService")
class DbUserDetailsServiceImpl(private val userDao: UserDao) : UserDetailsService {
    override fun loadUserByUsername(userId: String): UserDetails {
        return userDao.getUserById(userId) ?: throw ThainRuntimeException("user does not exist")
    }

}
