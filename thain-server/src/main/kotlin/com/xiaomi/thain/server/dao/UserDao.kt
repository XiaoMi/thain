package com.xiaomi.thain.server.dao

import com.xiaomi.thain.server.mapper.UserMapper
import com.xiaomi.thain.server.model.ThainUser
import com.xiaomi.thain.server.model.rq.AddUserRq
import com.xiaomi.thain.server.model.rq.UpdateUserRq
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Repository

/**
 * @author miaoyu
 */
@Repository
class UserDao(private val userMapper: UserMapper) {
    fun getUserById(userId: String): ThainUser? {
        return userMapper.getUserById(userId)
    }

    fun insertUser(addUserRq: AddUserRq) {
        val insertUser = ThainUser(
                userId = addUserRq.userId,
                username = addUserRq.username,
                passwordHash = BCryptPasswordEncoder().encode(addUserRq.password),
                email = addUserRq.email,
                admin = addUserRq.admin)
        userMapper.insertUser(insertUser)
    }

    fun insertThirdUser(user: ThainUser) {
        val insertUser: ThainUser = ThainUser(
                userId = user.userId,
                username = user.username,
                passwordHash = "thain")
        userMapper.insertUser(insertUser)
    }

    fun deleteUser(userId: String) {
        userMapper.deleteUser(userId)
    }

    val allUsers: List<ThainUser>
        get() = userMapper.allUsers

    fun updateUser(updateUserRq: UpdateUserRq): Boolean {
        if (userMapper.getUserById(updateUserRq.userId) != null) {
            userMapper.updateUserBySelective(updateUserRq)
            return true
        }
        return false
    }

}
