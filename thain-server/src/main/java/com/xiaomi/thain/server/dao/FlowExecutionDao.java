/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.dao;

import com.xiaomi.thain.common.model.FlowExecutionModel;
import com.xiaomi.thain.common.model.JobExecutionModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.server.mapper.FlowExecutionMapper;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * @author liangyongrui@xiaomi.com
 */
@Repository
public class FlowExecutionDao {

    @NonNull
    private final FlowExecutionMapper flowExecutionMapper;

    public FlowExecutionDao(@NonNull FlowExecutionMapper flowExecutionMapper) {
        this.flowExecutionMapper = flowExecutionMapper;
    }

    public boolean getAccessible(long flowExecutionId, @NonNull String userId, @Nullable Set<String> appIds) {
        return flowExecutionMapper.getUserAccessible(flowExecutionId, userId, appIds);
    }

    public List<FlowExecutionModel> getFlowExecutionList(long flowId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return flowExecutionMapper.getFlowExecutionList(flowId, offset, pageSize);
    }

    public long getFlowExecutionCount(long flowId) {
        return flowExecutionMapper.getFlowExecutionCount(flowId);
    }

    public Optional<FlowExecutionModel> getFlowExecution(long flowExecutionId) {
        return Optional.ofNullable(flowExecutionMapper.getFlowExecution(flowExecutionId));
    }

    public Optional<List<JobModel>> getJobModelList(long flowExecutionId) {
        return Optional.ofNullable(flowExecutionMapper.getJobModelList(flowExecutionId));
    }

    public List<JobExecutionModel> getJobExecutionModelList(long flowExecutionId) {
        return flowExecutionMapper.getJobExecutionModelList(flowExecutionId);
    }

    public boolean getAccessible(long flowExecutionId, @NonNull String appId) {
        return flowExecutionMapper.getAppIdAccessible(flowExecutionId, appId);
    }

    public List<Long> getRunningExecutionIdsByFlowId(long flowId) {
        return flowExecutionMapper.getRunningExecutionIdsByFlowId(flowId, 1);
    }
}
