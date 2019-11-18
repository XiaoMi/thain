/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model.dr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.sql.Timestamp;

/**
 * Date 19-5-17 下午12:07
 * 数据库查出的flow
 *
 * @author liangyongrui@xiaomi.com
 */
@AllArgsConstructor
@Builder(toBuilder = true)
public class FlowDr {

    public final long id;

    @NonNull
    public final String name;
    @NonNull
    public final String cron;
    @NonNull
    public final String modifyCallbackUrl;

    public final long pauseContinuousFailure;

    @NonNull
    public final String emailContinuousFailure;

    @NonNull
    public final String createUser;

    @NonNull
    public final String callbackUrl;

    @NonNull
    public final String callbackEmail;

    /**
     * 创建的appId,"thain" 为网页创建
     */
    @NonNull
    public final String createAppId;

    /**
     * 秒时间戳
     */
    public final long slaDuration;

    @NonNull
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

    /**
     * 创建时间
     */
    @NonNull
    public final Timestamp createTime;

    /**
     * 更新时间
     */
    @NonNull
    public final Timestamp updateTime;

    /**
     * 状态更新时间
     */
    @NonNull
    public final Timestamp statusUpdateTime;

    public final boolean deleted;

}
