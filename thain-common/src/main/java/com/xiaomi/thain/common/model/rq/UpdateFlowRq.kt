/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model.rq;

import com.xiaomi.thain.common.model.dr.FlowDr;
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
public class UpdateFlowRq {

    @NonNull
    public final Long id;
    @Nullable
    public final String name;
    @Nullable
    public final String cron;
    @Nullable
    public final String modifyCallbackUrl;
    @Nullable
    public final Long pauseContinuousFailure;
    @Nullable
    public final String emailContinuousFailure;
    @Nullable
    public final String callbackUrl;
    @Nullable
    public final String callbackEmail;
    /**
     * 秒时间戳
     */
    @Nullable
    public final Long slaDuration;
    @Nullable
    public final String slaEmail;
    public final boolean slaKill;

    /**
     * 最后一次运行状态,com.xiaomi.thain.common.constant.FlowLastRunStatus
     */
    @Nullable
    public final Integer lastRunStatus;

    /**
     * 调度状态，1 调度中、2 暂停调度、（3 未设置调度{只运行一次的任务}）
     */
    @Nullable
    public final Integer schedulingStatus;


    public static UpdateFlowRq getInstance(@NonNull AddFlowRq addFlowRq, long id) {
        return UpdateFlowRq.builder()
                .id(id)
                .name(addFlowRq.name)
                .cron(addFlowRq.cron)
                .modifyCallbackUrl(addFlowRq.modifyCallbackUrl)
                .pauseContinuousFailure(addFlowRq.pauseContinuousFailure)
                .emailContinuousFailure(addFlowRq.emailContinuousFailure)
                .callbackUrl(addFlowRq.callbackUrl)
                .callbackEmail(addFlowRq.callbackEmail)
                .slaDuration(addFlowRq.slaDuration)
                .slaEmail(addFlowRq.slaEmail)
                .slaKill(addFlowRq.slaKill)
                .lastRunStatus(addFlowRq.lastRunStatus)
                .schedulingStatus(addFlowRq.schedulingStatus)
                .build();
    }

    public static UpdateFlowRq getInstance(@NonNull FlowDr flowDr) {
        return UpdateFlowRq.builder()
                .id(flowDr.id)
                .name(flowDr.name)
                .cron(flowDr.cron)
                .modifyCallbackUrl(flowDr.modifyCallbackUrl)
                .pauseContinuousFailure(flowDr.pauseContinuousFailure)
                .emailContinuousFailure(flowDr.emailContinuousFailure)
                .callbackUrl(flowDr.callbackUrl)
                .callbackEmail(flowDr.callbackEmail)
                .slaDuration(flowDr.slaDuration)
                .slaEmail(flowDr.slaEmail)
                .slaKill(flowDr.slaKill)
                .lastRunStatus(flowDr.lastRunStatus)
                .schedulingStatus(flowDr.schedulingStatus)
                .build();
    }
}
