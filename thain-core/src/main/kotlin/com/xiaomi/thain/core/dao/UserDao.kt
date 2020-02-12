/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.dao

import com.xiaomi.thain.core.entity.ThainUser
import com.xiaomi.thain.core.mapper.UserMapper
import org.apache.ibatis.session.SqlSessionFactory
import org.slf4j.LoggerFactory

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
class UserDao(private val sqlSessionFactory: SqlSessionFactory) {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    /**
     * 自动释放sqlSession，事务执行mapper
     *
     * @param function function 是一个事务
     * @return 自定义的返回值
     */
    private fun <T> execute(function: (UserMapper) -> T?): T? {
        try {
            sqlSessionFactory.openSession().use { sqlSession ->
                val apply = function(sqlSession.getMapper(UserMapper::class.java))
                sqlSession.commit()
                return apply
            }
        } catch (e: Exception) {
            log.error("", e)
            return null
        }
    }

    val adminUsers: List<ThainUser>
        get() = execute { it.adminUsers } ?: emptyList()

}
