/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.config;

import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.core.ThainFacade;
import com.xiaomi.thain.core.process.ProcessEngineConfiguration;
import com.xiaomi.thain.core.scheduler.SchedulerEngineConfiguration;
import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * @author liangyongrui
 */
@Configuration
public class AppConfig {

    @NonNull
    private final Environment env;

    public AppConfig(@NonNull Environment environment) {
        this.env = environment;
    }

    @Bean("thainFacade")
    public ThainFacade createProcessEngineFacade(@NonNull @Qualifier("mysqlDataSource") DataSource dataSource)
            throws ThainException, IOException, SQLException {

        val processEngineConfiguration = ProcessEngineConfiguration.builder()
                .mailHost(env.getProperty("mail.host", ""))
                .mailSender(env.getProperty("mail.sender", ""))
                .mailSenderPassword(env.getProperty("mail.sender.username", ""))
                .mailSenderUsername(env.getProperty("mail.sender.password", ""))
                .dataSource(dataSource)
                .flowExecutionJobExecutionThreadPoolCoreSize(
                        Integer.valueOf(env.getProperty("flow.execution.job.execution.thread.pool.core.size", "3")))
                .flowExecutionJobExecutionThreadPoolMaximumSize(
                        Integer.valueOf(env.getProperty("flow.execution.job.execution.thread.pool.maximum.size", "5")))
                .flowExecutionJobExecutionThreadPoolKeepAliveSecond(
                        Long.valueOf(env.getProperty("flow.execution.job.execution.thread.pool.keep.alive.second", "60")))
                .flowExecutionThreadPoolCoreSize(Integer.valueOf(env.getProperty("flowExecution.thread.pool.core.size", "5")))
                .flowExecutionThreadPoolMaximumSize(Integer.valueOf(env.getProperty("flowExecution.thread.pool.maximum.size", "10")))
                .flowExecutionThreadPoolKeepAliveSecond(Long.valueOf(env.getProperty("flowExecution.thread.pool.keep.alive.second", "60")))
                .dataReserveDays(Integer.valueOf(env.getProperty("dataReserveDays", "30")))
                .initLevel(env.getProperty("datasource.initialization.level", "-1"))
                .build();
        val in = getClass().getResourceAsStream("/quartz.properties");
        val schedulerEngineConfiguration = SchedulerEngineConfiguration.getInstanceByInputStream(in);
        schedulerEngineConfiguration.properties.put("org.quartz.dataSource.job_scheduler.URL",
                env.getProperty("spring.datasource.url", ""));
        schedulerEngineConfiguration.properties.put("org.quartz.dataSource.job_scheduler.user",
                env.getProperty("spring.datasource.username", ""));
        schedulerEngineConfiguration.properties.put("org.quartz.dataSource.job_scheduler.password",
                env.getProperty("spring.datasource.password", ""));
        return ThainFacade.getInstance(processEngineConfiguration, schedulerEngineConfiguration);
    }

}
