/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.mapper;

import com.xiaomi.thain.common.model.FlowExecutionModel;
import com.xiaomi.thain.common.model.dp.AddFlowExecutionDp;
import com.xiaomi.thain.common.model.dr.FlowExecutionDr;
import lombok.NonNull;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
public interface FlowExecutionMapper {

    int updateLogs(@Param("flowExecutionId") long flowExecutionId, @NonNull @Param("content") String content);

    int updateFlowExecutionStatus(@Param("flowExecutionId") long flowExecutionId, @Param("status") int status);

    int cleanUpExpiredFlowExecution(int dataReserveDays);

    @Nullable
    FlowExecutionDr getFlowExecution(long flowExecutionId);

    /**
     * 获取制定id的最近几条执行记录
     *
     * @param flowId  flowId
     * @param numbers 最近numbers 条
     * @return 记录
     */
    List<FlowExecutionModel> getLatest(@Param("flowId") long flowId, @Param("numbers") long numbers);

    int addFlowExecution(@NonNull AddFlowExecutionDp addFlowExecutionDp);

    /**
     * 设置flowExecution的心跳为当前时间
     */
    int setFlowExecutionHeartbeat(@NonNull List<Long> flowExecutionIds);

    /**
     * 获取超过1min没心跳的任务
     */
    List<FlowExecutionDr> getDead();

    int reWaiting(@NonNull List<Long> flowExecutionIds);

    int updateHostInfo(@Param("id") long id, @Param("hostInfo") String hostInfo);
}
