/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.mapper;

import com.xiaomi.thain.common.model.FlowExecutionModel;
import com.xiaomi.thain.common.model.JobExecutionModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.common.model.dr.FlowExecutionDr;
import lombok.NonNull;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author liangyongrui@xiaomi.com
 */
@Component
public interface FlowExecutionMapper {

    boolean getAppIdAccessible(@Param("flowExecutionId") long flowExecutionId, @NonNull @Param("appId") String appId);

    List<FlowExecutionModel> getFlowExecutionList(@Param("flowId") long flowId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("select count(*) from thain_flow_execution where flow_id = #{flowId}")
    long getFlowExecutionCount(@Param("flowId") long flowId);

    @Nullable
    FlowExecutionDr getFlowExecution(long flowExecutionId);

    List<JobModel> getJobModelList(long flowExecutionId);

    List<JobExecutionModel> getJobExecutionModelList(long flowExecutionId);

    /**
     * get execution by flowId
     *
     * @param flowId flowId
     * @param status status
     * @return list execution Id
     */
    List<Long> getRunningExecutionIdsByFlowId(@Param("flowId") long flowId, @Param("status") int status);

}
