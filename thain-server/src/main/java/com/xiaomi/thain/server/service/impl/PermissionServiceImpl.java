/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.service.impl;

import com.xiaomi.thain.server.dao.FlowDao;
import com.xiaomi.thain.server.dao.FlowExecutionDao;
import com.xiaomi.thain.server.service.PermissionService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Set;
import javax.annotation.Nullable;

/**
 * @author liangyongrui@xiaomi.com
 * @date 18-12-6 上午11:47
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    @NonNull
    private final FlowExecutionDao flowExecutionDao;
    @NonNull
    private final FlowDao flowDao;

    public PermissionServiceImpl(@NonNull FlowExecutionDao flowExecutionDao, @NonNull FlowDao flowDao) {
        this.flowExecutionDao = flowExecutionDao;
        this.flowDao = flowDao;
    }

    @Override
    public boolean getFlowAccessible(long flowId, @NonNull String userId, @Nullable Set<String> appIds) {
        return flowDao.getAccessible(flowId, userId, appIds);
    }

    @Override
    public boolean getFlowAccessible(long flowId, @NonNull String appId) {
        return flowDao.getAccessible(flowId, appId);
    }

    @Override
    public boolean getFlowExecutionAccessible(long flowExecutionId, @NonNull String userId, @Nullable Set<String> appIds) {
        return flowExecutionDao.getAccessible(flowExecutionId, userId, appIds);
    }

    @Override
    public boolean getFlowExecutionAccessible(long flowExecutionId, @NonNull String appId) {
        return flowExecutionDao.getAccessible(flowExecutionId, appId);
    }

}
