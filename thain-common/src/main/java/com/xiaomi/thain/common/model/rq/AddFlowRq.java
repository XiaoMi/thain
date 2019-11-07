/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model.rq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nullable;

/**
 * add flow model
 *
 * @author liangyongrui@xiaomi.com
 */
@AllArgsConstructor
@Builder(toBuilder = true)
public class AddFlowRq {

    /**
     * 添加成功后 id存在
     */
    @Nullable
    public final Long id;

    @NonNull
    public final String name;
    @Nullable
    public final String cron;
    @Nullable
    public final String modifyCallbackUrl;
    @Nullable
    public final Long pauseContinuousFailure;
    @Nullable
    public final String emailContinuousFailure;
    @NonNull
    public final String createUser;
    @Nullable
    public final String callbackUrl;
    @Nullable
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

}
