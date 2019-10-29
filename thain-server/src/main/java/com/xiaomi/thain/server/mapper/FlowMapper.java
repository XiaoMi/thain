/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.mapper;

import com.xiaomi.thain.common.model.FlowModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.server.model.sp.FlowListSp;
import lombok.NonNull;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * @author liangyongrui@xiaomi.com
 */
@Component
public interface FlowMapper {

    boolean getUserAccessible(@Param("flowId") long flowId, @NonNull @Param("userId") String userId,
                              @Nullable @Param("appIds") Set<String> appIds);

    boolean getAppIdAccessible(@Param("flowId") long flowId, @NonNull @Param("appId") String appId);

    List<FlowModel> getFlowList(@NonNull FlowListSp flowListSp);

    Long getFlowListCount(@NonNull FlowListSp flowListSp);

    boolean flowExist(long flowId);

    void updateAppId(@Param("flowId") long flowId, @NonNull @Param("appId") String appId);

    Long getFlowIdByFlowExecutionId(long flowExecutionId);

    FlowModel getFlow(long flowId);

    List<JobModel> getJobModelList(long flowId);

}
