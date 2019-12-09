/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.sql.Timestamp;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Date 19-5-17 下午12:07
 * flow model 和数据库交互的类
 *
 * @author liangyongrui@xiaomi.com
 */
@AllArgsConstructor
@Builder(toBuilder = true)
public class FlowModel {

    public final long id;

    @Nullable
    public final String name;

    @Nullable
    public final String cron;

    @Nullable
    public final String modifyCallbackUrl;

    public final long pauseContinuousFailure;

    @Nullable
    public final String emailContinuousFailure;

    @Nullable
    public final String createUser;

    @Nullable
    public final String callbackUrl;

    @Nullable
    public final String callbackEmail;

    /**
     * 创建的appId,"thain" 为网页创建
     */
    @Nullable
    public final String createAppId;

    /**
     * 秒时间戳
     */
    public final long slaDuration;

    @Nullable
    public final String slaEmail;

    public final boolean slaKill;

    /**
     * 最后一次运行状态,com.xiaomi.thain.common.constant.FlowLastRunStatus
     */
    public final int lastRunStatus;

    /**
     * 调度状态，1 调度中、2 暂停调度、（3 未设置调度{只运行一次的任务}）
     */
    public final int schedulingStatus;

    public final int retryNumber;

    public final int timeInterval;

    /**
     * 创建时间
     */
    @Nullable
    public final Timestamp createTime;

    /**
     * 更新时间
     */
    @Nullable
    public final Timestamp updateTime;

    /**
     * 状态更新时间
     */
    @Nullable
    public final Timestamp statusUpdateTime;

    public final boolean deleted;

    public Optional<Long> getCreateTime() {
        return Optional.ofNullable(createTime).map(Timestamp::getTime);
    }

    public Optional<Long> getUpdateTime() {
        return Optional.ofNullable(updateTime).map(Timestamp::getTime);
    }

    public Optional<Long> getStatusUpdateTime() {
        return Optional.ofNullable(statusUpdateTime).map(Timestamp::getTime);
    }
}
