/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.service;

import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.model.FlowExecutionModel;
import com.xiaomi.thain.common.model.JobExecutionModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.common.model.dr.FlowExecutionDr;

import java.util.List;

/**
 * Date 19-6-10 下午6:25
 *
 * @author liangyongrui@xiaomi.com
 */
public interface FlowExecutionService {

    /**
     * 获取flowExecution列表
     */
    List<FlowExecutionModel> getFlowExecutionList(long flowId, int page, int pageSize);

    /**
     * 获取flowId现存的flowExecution数量
     */
    long getFlowExecutionCount(long flowId);

    void killFlowExecution(long flowExecutionId) throws ThainException;

    FlowExecutionDr getFlowExecution(long flowExecutionId) throws ThainException;

    List<JobModel> getJobModelList(long flowExecutionId) throws ThainException;

    List<JobExecutionModel> getJobExecutionModelList(long flowExecutionId) throws ThainException;

    /**
     * kill execution by flowId
     *
     * @param flowId flowId
     * @return {@code true} if kill success and has Running execution
     * @throws ThainException kill failure
     */
    boolean killFlowExecutionsByFlowId(long flowId) throws ThainException;
}
