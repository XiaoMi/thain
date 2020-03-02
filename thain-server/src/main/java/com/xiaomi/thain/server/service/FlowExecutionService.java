/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service;

import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.model.JobExecutionModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.common.model.dr.FlowExecutionDr;
import com.xiaomi.thain.core.ThainFacade;
import com.xiaomi.thain.server.dao.FlowExecutionDao;
import lombok.NonNull;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Date 19-6-10 下午8:35
 *
 * @author liangyongrui@xiaomi.com
 */
@Service
public class FlowExecutionService {

    @NonNull
    private final FlowExecutionDao flowExecutionDao;
    @NonNull
    private final ThainFacade thainFacade;

    public FlowExecutionService(@NonNull FlowExecutionDao flowExecutionDao, @NonNull ThainFacade thainFacade) {
        this.flowExecutionDao = flowExecutionDao;
        this.thainFacade = thainFacade;
    }

    public List<FlowExecutionDr> getFlowExecutionList(long flowId, int page, int pageSize) {
        return flowExecutionDao.getFlowExecutionList(flowId, page, pageSize);
    }

    public long getFlowExecutionCount(long flowId) {
        return flowExecutionDao.getFlowExecutionCount(flowId);
    }

    public void killFlowExecution(Long flowId, Long flowExecutionId, String appId, String username) throws ThainException {
        thainFacade.killFlowExecution(flowId, flowExecutionId, false, appId, username);
    }

    public FlowExecutionDr getFlowExecution(long flowExecutionId) throws ThainException {
        return Optional.ofNullable(flowExecutionDao.getFlowExecution(flowExecutionId))
                .orElseThrow(() -> new ThainException("flowExecution id does not exist：" + flowExecutionId));
    }

    public List<JobModel> getJobModelList(long flowExecutionId) {
        return flowExecutionDao.getJobModelList(flowExecutionId);
    }

    public List<JobExecutionModel> getJobExecutionModelList(long flowExecutionId) {
        return flowExecutionDao.getJobExecutionModelList(flowExecutionId);
    }

    public boolean killFlowExecutionsByFlowId(long flowId, String appId, String username) throws ThainException {
        val executionIds = flowExecutionDao.getRunningExecutionIdsByFlowId(flowId);
        if (!executionIds.isEmpty()) {
            for (val executionId : executionIds) {
                killFlowExecution(flowId, executionId, appId, username);
            }
            return true;
        }
        return false;
    }
}
