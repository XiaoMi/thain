/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.mapper;

import com.xiaomi.thain.common.model.FlowModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.common.model.rq.AddFlowRq;
import com.xiaomi.thain.common.model.rq.UpdateFlowRq;
import lombok.NonNull;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
public interface FlowMapper {

    /**
     * 数据库插入flow，成功flowModel插入id
     */
    int addFlow(@NonNull AddFlowRq flowRq);

    /**
     * 更新
     */
    int updateFlow(UpdateFlowRq updateFlowRq);

    int deleteFlow(long flowId);

    @Nullable
    com.xiaomi.thain.common.model.dr.FlowDr getFlow(long flowId);

    /**
     * 更新最后一次运行状态
     */
    int updateLastRunStatus(@Param("flowId") long flowId, @Param("lastRunStatus") int lastRunStatus);

    int addJobList(@NonNull List<JobModel> jobModelList);

    int invalidJobList(@Param("flowId") long flowId);

    int deleteJob(long flowId);

    int updateSchedulingStatus(@Param("flowId") long flowId, @Param("schedulingStatus") int schedulingStatus);

    /**
     * 获取所有的flow id
     */
    List<Long> getAllFlowIds();
}
