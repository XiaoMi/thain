/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service.impl;

import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.model.FlowExecutionModel;
import com.xiaomi.thain.common.model.JobExecutionModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.core.ThainFacade;
import com.xiaomi.thain.server.dao.FlowExecutionDao;
import com.xiaomi.thain.server.service.FlowExecutionService;
import lombok.NonNull;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Date 19-6-10 下午8:35
 *
 * @author liangyongrui@xiaomi.com
 */
@Service
public class FlowExecutionServiceImpl implements FlowExecutionService {

    @NonNull
    private final FlowExecutionDao flowExecutionDao;
    @NonNull
    private final ThainFacade thainFacade;

    public FlowExecutionServiceImpl(@NonNull FlowExecutionDao flowExecutionDao, @NonNull ThainFacade thainFacade) {
        this.flowExecutionDao = flowExecutionDao;
        this.thainFacade = thainFacade;
    }

    @Override
    public List<FlowExecutionModel> getFlowExecutionList(long flowId, int page, int pageSize) {
        return flowExecutionDao.getFlowExecutionList(flowId, page, pageSize);
    }

    @Override
    public long getFlowExecutionCount(long flowId) {
        return flowExecutionDao.getFlowExecutionCount(flowId);
    }

    @Override
    public void killFlowExecution(long flowExecutionId) throws ThainException {
        thainFacade.killFlowExecution(flowExecutionId, false);
    }

    @Override
    public FlowExecutionModel getFlowExecution(long flowExecutionId) throws ThainException {
        return flowExecutionDao.getFlowExecution(flowExecutionId).orElseThrow(
                () -> new ThainException("flowExecution id does not exist：" + flowExecutionId));
    }

    @Override
    public List<JobModel> getJobModelList(long flowExecutionId) throws ThainException {
        return flowExecutionDao.getJobModelList(flowExecutionId).orElseThrow(
                () -> new ThainException("flowExecution id does not exist：" + flowExecutionId));
    }

    @Override
    public List<JobExecutionModel> getJobExecutionModelList(long flowExecutionId) {
        return flowExecutionDao.getJobExecutionModelList(flowExecutionId);
    }

    @Override
    public boolean killFlowExecutionsByFlowId(long flowId) throws ThainException {
        val executionIds = flowExecutionDao.getRunningExecutionIdsByFlowId(flowId);
        if (!executionIds.isEmpty()) {
            for (val executionId : executionIds) {
                killFlowExecution(executionId);
            }
            return true;
        }
        return false;
    }
}
