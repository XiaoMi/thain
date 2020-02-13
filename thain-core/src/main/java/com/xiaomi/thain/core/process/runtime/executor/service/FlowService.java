/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process.runtime.executor.service;

import com.xiaomi.thain.common.constant.FlowLastRunStatus;
import com.xiaomi.thain.core.dao.FlowDao;
import com.xiaomi.thain.core.process.ProcessEngineStorage;
import lombok.NonNull;

/**
 * Date 19-5-21 上午10:46
 * 不影响流程执行的flow相关操作
 *
 * @author liangyongrui@xiaomi.com
 */
public class FlowService {

    public final long flowId;

    @NonNull
    public final FlowDao flowDao;

    private FlowService(long flowId,
                        @NonNull ProcessEngineStorage processEngineStorage) {
        this.flowId = flowId;
        this.flowDao = processEngineStorage.getFlowDao();

    }

    public static FlowService getInstance(long flowId,
                                          @NonNull ProcessEngineStorage processEngineStorage) {
        return new FlowService(flowId, processEngineStorage);
    }

    /**
     * 开始运行flow
     * 设置当前的flow状态为 正在运行
     */
    void startFlow() {
        flowDao.updateLastRunStatus(flowId, FlowLastRunStatus.RUNNING);
    }

    void endFlow(@NonNull FlowLastRunStatus endStatus) {
        flowDao.updateLastRunStatus(flowId, endStatus);
    }

}
