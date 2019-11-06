/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process;

import com.xiaomi.thain.common.exception.ThainMissRequiredArgumentsException;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;

import javax.sql.DataSource;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-16 下午8:35
 */
@ToString
@Builder(builderMethodName = "doNotUseIt")
public class ProcessEngineConfiguration {

    /**
     * 发送邮件相关属性
     */
    @NonNull
    public final String mailHost;
    @NonNull
    public final String mailSender;
    @NonNull
    public final String mailSenderUsername;
    @NonNull
    public final String mailSenderPassword;

    /**
     * 数据源
     */
    @NonNull
    public final DataSource dataSource;

    /**
     * flowExecution线程池
     */
    @NonNull
    public final Integer flowExecutionThreadPoolCoreSize;

    /**
     * 每个flowExecution的jobExecution线程池
     */
    @NonNull
    public final Integer flowExecutionJobExecutionThreadPoolCoreSize;

    /**
     * 数据保留天数
     */
    @NonNull
    public final Integer dataReserveDays;

    @NonNull
    public final String initLevel;
   
    public static ProcessEngineConfigurationBuilder builder() throws ThainMissRequiredArgumentsException {
        return doNotUseIt();
    }

}
