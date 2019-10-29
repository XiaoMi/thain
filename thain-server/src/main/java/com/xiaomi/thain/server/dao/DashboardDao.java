/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.dao;

import com.xiaomi.thain.server.model.dr.SourceAndCountDr;
import com.xiaomi.thain.server.model.dr.StatusAndCountDr;
import com.xiaomi.thain.server.model.dr.StatusAndCountAndTimeDr;
import com.xiaomi.thain.server.mapper.DashboardMapper;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author miaoyu3@xiaomi.com
 * @date 19-7-30下午5:40
 */
@Repository
public class DashboardDao {
    @NonNull
    private final DashboardMapper dashboardMapper;

    public DashboardDao(@NonNull DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    public List<StatusAndCountDr> getScheduleStatusCount(@Nullable String[] filterSource) {
        return dashboardMapper.getScheduleStatusCount(filterSource);
    }

    public List<SourceAndCountDr> getFlowSourceCount(@Nullable String[] filterScheduleStatus) {
        return dashboardMapper.getFlowSourceCount(filterScheduleStatus);
    }

    public List<StatusAndCountDr> getFlowExecutionStatusCount(@NonNull Long[] period) {
        return dashboardMapper.getFlowExecutionStatusCount(period);
    }

    public List<StatusAndCountDr> getJobExecutionStatusCount(@NonNull Long[] period) {
        return dashboardMapper.getJobExecutionStatusCount(period);
    }

    public int getRunningFlowCount(@Nullable String[] filterSource, @Nullable String[] filterScheduleStatus) {
        return dashboardMapper.getRunningFlowCount(filterSource, filterScheduleStatus);
    }

    public int getRunningJobCount(@Nullable String[] filterSource, @Nullable String[] filterScheduleStatus) {
        return dashboardMapper.getRunningJobCount(filterSource, filterScheduleStatus);
    }

    public int getIncreaseFlowCount(@NonNull Long[] period) {
        return dashboardMapper.getIncreaseFlowCount(period);
    }

    public int getIncreaseJobCount(@NonNull Long[] period) {
        return dashboardMapper.getIncreaseJobCount(period);
    }

    public List<StatusAndCountAndTimeDr> getStatusHistoryCount(Long[] period) {
        return dashboardMapper.getStatusHistoryCount(period);
    }
}
