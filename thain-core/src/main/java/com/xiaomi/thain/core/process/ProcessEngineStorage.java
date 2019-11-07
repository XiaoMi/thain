/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process;

import com.xiaomi.thain.common.exception.ThainMissRequiredArgumentsException;
import com.xiaomi.thain.common.model.FlowExecutionModel;
import com.xiaomi.thain.common.model.dp.AddFlowExecutionDp;
import com.xiaomi.thain.common.model.dr.FlowExecutionDr;
import com.xiaomi.thain.core.dao.FlowDao;
import com.xiaomi.thain.core.dao.FlowExecutionDao;
import com.xiaomi.thain.core.dao.JobDao;
import com.xiaomi.thain.core.dao.JobExecutionDao;
import com.xiaomi.thain.core.process.runtime.heartbeat.FlowExecutionHeartbeat;
import com.xiaomi.thain.core.process.runtime.notice.MailNotice;
import com.xiaomi.thain.core.process.service.ComponentService;
import com.xiaomi.thain.core.process.service.MailService;
import com.xiaomi.thain.core.thread.pool.ThainThreadPool;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.LongFunction;

/**
 * Date 19-5-31 下午7:45
 *
 * @author liangyongrui@xiaomi.com
 */
@ToString
@Builder(builderMethodName = "doNotUseIt")
public final class ProcessEngineStorage {

    @NonNull
    public final ThainThreadPool flowExecutionThreadPool;
    @NonNull
    public final String processEngineId;
    @NonNull
    public final FlowDao flowDao;
    @NonNull
    public final FlowExecutionDao flowExecutionDao;
    @NonNull
    public final JobDao jobDao;
    @NonNull
    public final JobExecutionDao jobExecutionDao;
    @NonNull
    public final MailService mailService;
    @NonNull
    public final ComponentService componentService;
    @NonNull
    private final LongFunction<ThainThreadPool> flowExecutionJobExecutionThreadPool;
    @NonNull
    public final LinkedBlockingQueue<AddFlowExecutionDp> flowExecutionWaitingQueue;
    @NonNull
    public final FlowExecutionHeartbeat flowExecutionHeartbeat;

    public MailNotice getMailNotice(@NonNull String noticeEmail) {
        return MailNotice.getInstance(mailService, noticeEmail);
    }

    public ThainThreadPool flowExecutionJobThreadPool(long flowExecutionId) {
        return flowExecutionJobExecutionThreadPool.apply(flowExecutionId);
    }

    public static ProcessEngineStorageBuilder builder() throws ThainMissRequiredArgumentsException {
        return doNotUseIt();
    }
}
