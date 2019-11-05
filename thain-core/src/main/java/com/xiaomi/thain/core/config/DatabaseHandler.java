/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.config;

import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.core.mapper.FlowExecutionMapper;
import com.xiaomi.thain.core.mapper.FlowMapper;
import com.xiaomi.thain.core.mapper.JobExecutionMapper;
import com.xiaomi.thain.core.mapper.JobMapper;
import com.xiaomi.thain.core.mapper.UserMapper;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

/**
 * Date 19-5-17 下午5:26
 *
 * @author liangyongrui@xiaomi.com
 */
@Log4j2
public class DatabaseHandler {

    private DatabaseHandler() {

    }

    /**
     * 新建一个SqlSessionFactory
     *
     * @param dataSource dataSource
     * @return SqlSessionFactory
     */
    private static SqlSessionFactory newSqlSessionFactory(@NonNull DataSource dataSource) {
        try {
            val transactionFactory = new JdbcTransactionFactory();
            val environment = new Environment("development", transactionFactory, dataSource);
            val configuration = new Configuration(environment);
            configuration.addMapper(UserMapper.class);
            configuration.addMapper(FlowMapper.class);
            configuration.addMapper(JobMapper.class);
            configuration.addMapper(FlowExecutionMapper.class);
            configuration.addMapper(JobExecutionMapper.class);
            configuration.setMapUnderscoreToCamelCase(true);
            return new SqlSessionFactoryBuilder().build(configuration);
        } catch (Exception e) {
            log.error("", e);
            throw new ThainRuntimeException(e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory(@NonNull DataSource dataSource) {
        return newSqlSessionFactory(dataSource);
    }
}
