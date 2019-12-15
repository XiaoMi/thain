/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.config

import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.core.mapper.*
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import org.slf4j.LoggerFactory
import javax.sql.DataSource

/**
 * Date 19-5-17 下午5:26
 *
 * @author liangyongrui@xiaomi.com
 */
object DatabaseHandler {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    /**
     * 新建一个SqlSessionFactory
     *
     * @param dataSource dataSource
     * @return SqlSessionFactory
     */
    @JvmStatic
    fun newSqlSessionFactory(dataSource: DataSource, dataReserveDays: Int): SqlSessionFactory {
        return try {
            val transactionFactory = JdbcTransactionFactory()
            val environment = Environment("development", transactionFactory, dataSource)
            val configuration = Configuration(environment)
            configuration.addMapper(UserMapper::class.java)
            configuration.addMapper(FlowMapper::class.java)
            configuration.addMapper(JobMapper::class.java)
            configuration.addMapper(FlowExecutionMapper::class.java)
            configuration.addMapper(JobExecutionMapper::class.java)
            configuration.isMapUnderscoreToCamelCase = true
            configuration.variables["dataReserveDays"] = dataReserveDays
            SqlSessionFactoryBuilder().build(configuration)
        } catch (e: Exception) {
            log.error("", e)
            throw ThainRuntimeException(e)
        }
    }
}