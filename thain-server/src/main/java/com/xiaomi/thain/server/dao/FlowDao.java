/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.dao;

import com.xiaomi.thain.common.model.FlowModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.server.model.sp.FlowListSp;
import com.xiaomi.thain.server.mapper.FlowMapper;
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
public class FlowDao {

    @NonNull
    private final FlowMapper flowMapper;

    public FlowDao(@NonNull FlowMapper flowMapper) {
        this.flowMapper = flowMapper;
    }

    public boolean getAccessible(long flowId, @NonNull String userId, @Nullable Set<String> appIds) {
        return flowMapper.getUserAccessible(flowId, userId, appIds);
    }

    public List<FlowModel> getFlowList(@NonNull FlowListSp flowListSp) {
        return flowMapper.getFlowList(flowListSp);
    }

    public Long getFlowListCount(@NonNull FlowListSp flowListSp) {
        return flowMapper.getFlowListCount(flowListSp);
    }

    public boolean flowExist(long flowId) {
        return flowMapper.flowExist(flowId);
    }

    public void updateAppId(long flowId, @NonNull String appId) {
        flowMapper.updateAppId(flowId, appId);
    }

    public Optional<FlowModel> getFlow(long flowId) {
        return Optional.ofNullable(flowMapper.getFlow(flowId));
    }

    public List<JobModel> getJobModelList(long flowId) {
        return flowMapper.getJobModelList(flowId);
    }

    public boolean getAccessible(long flowId, @NonNull String appId) {
        return flowMapper.getAppIdAccessible(flowId, appId);
    }
}
