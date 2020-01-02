/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service;

import com.xiaomi.thain.common.constant.FlowExecutionStatus;
import com.xiaomi.thain.server.dao.DashboardDao;
import com.xiaomi.thain.server.model.dr.SourceAndCountDr;
import com.xiaomi.thain.server.model.dr.StatusAndCountAndTimeDr;
import com.xiaomi.thain.server.model.dr.StatusAndCountDr;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author miaoyu3@xiaomi.com
 * @date 19-7-30下午4:44
 */
@Slf4j
@Service
public class DashboardService {
    @NonNull
    private final DashboardDao dashboardDao;

    public DashboardService(@NonNull DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    /**
     * 统计不同调度状态的任务
     *
     * @param filterSource 需要过滤的已接入系统
     * @return 统计列表
     */
    public List<StatusAndCountDr> getScheduleStatusCount(@Nullable String[] filterSource) {
        return dashboardDao.getScheduleStatusCount(filterSource);
    }

    /**
     * 统计不同已接入系统的任务
     *
     * @param filterScheduleStatus 需要过滤的调度状态
     * @return 统计列表
     */
    public List<SourceAndCountDr> getFlowSourceCount(@Nullable String[] filterScheduleStatus) {
        return dashboardDao.getFlowSourceCount(filterScheduleStatus);
    }

    /**
     * 统计flow运行状况
     *
     * @param period 统计时间段
     * @return 统计列表
     */
    public List<StatusAndCountDr> getFlowExecutionStatusCount(@NonNull Long[] period) {
        return dashboardDao.getFlowExecutionStatusCount(period);
    }

    /**
     * 统计job运行状况
     *
     * @param period 统计时间段
     * @return 统计列表
     */
    public List<StatusAndCountDr> getJobExecutionStatusCount(@NonNull Long[] period) {
        return dashboardDao.getJobExecutionStatusCount(period);
    }

    /**
     * 统计正在运行的flow
     *
     * @param filterSource         需要过滤的已接入系统
     * @param filterScheduleStatus 需要过滤的调度状态
     * @return 总数
     */
    public int getRunningFlowCount(@Nullable String[] filterSource, @Nullable String[] filterScheduleStatus) {
        return dashboardDao.getRunningFlowCount(filterSource, filterScheduleStatus);
    }

    /**
     * 统计正在运行的job
     *
     * @param filterSource         需要过滤的已接入系统
     * @param filterScheduleStatus 需要过滤的调度状态
     * @return 总数
     */
    public int getRunningJobCount(@Nullable String[] filterSource, @Nullable String[] filterScheduleStatus) {
        return dashboardDao.getRunningJobCount(filterSource, filterScheduleStatus);
    }

    /**
     * 统计增长的flow
     *
     * @param period 统计时间段
     * @return 总数
     */
    public int getIncreaseFlowCount(@NonNull Long[] period) {
        return dashboardDao.getIncreaseFlowCount(period);
    }

    /**
     * 统计增长的job
     *
     * @param period 统计时间段
     * @return 总数
     */
    public int getIncreaseJobCount(@NonNull Long[] period) {
        return dashboardDao.getIncreaseJobCount(period);
    }

    /**
     * 统计历史执行情况
     *
     * @param period   统计时间段
     * @param maxPointNum 折线图的点数
     * @return 统计列表
     */
    public List<StatusAndCountAndTimeDr> getStatusHistoryCount(@NonNull Long[] period, int maxPointNum) {
        val resList = dashboardDao.getStatusHistoryCount(period);
        if (resList.isEmpty()) {
            return resList;
        }
        long startTime = period[0];
        long endTime = period[1];
        long interval = (endTime - startTime) / maxPointNum;
        val formatResult = new ArrayList<StatusAndCountAndTimeDr>(maxPointNum * 2);
        for (long time = startTime; time < endTime; time += interval) {
            formatResult.add(StatusAndCountAndTimeDr.builder()
                    .status(FlowExecutionStatus.SUCCESS.code)
                    .count(0)
                    .time(time + "~" + (time + interval))
                    .build()
            );
            formatResult.add(StatusAndCountAndTimeDr.builder()
                    .status(FlowExecutionStatus.ERROR.code)
                    .count(0)
                    .time(time + "~" + (time + interval))
                    .build()
            );
        }
        resList.forEach(t -> {
            long time = Long.parseLong(t.time);
            int index = Math.toIntExact((time - startTime) / interval);
            if (t.status == FlowExecutionStatus.SUCCESS.code) {
                index = 2 * index;
            } else {
                index = 2 * index + 1;
            }
            val result = formatResult.get(index);
            formatResult.set(index, result.toBuilder().count(result.count + t.count).build());
        });
        return formatResult;
    }
}
