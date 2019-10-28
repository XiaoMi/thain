/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service;

import com.xiaomi.thain.server.entity.response.StatusHistoryCount;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author miaoyu3@xiaomi.com
 * @date 19-7-30下午4:44
 */
@Service
public interface DashboardService {
    /**
     * 统计不同调度状态的任务
     *
     * @param filterSource 需要过滤的已接入系统
     * @return 统计列表
     */
    List<Map> getScheduleStatusCount(@Nullable String[] filterSource);

    /**
     * 统计不同已接入系统的任务
     *
     * @param filterScheduleStatus 需要过滤的调度状态
     * @return 统计列表
     */
    List<Map> getFlowSourceCount(@Nullable String[] filterScheduleStatus);

    /**
     * 统计flow运行状况
     *
     * @param period 统计时间段
     * @return 统计列表
     */
    List<Map> getFlowExecutionStatusCount(@NonNull Long[] period);

    /**
     * 统计job运行状况
     *
     * @param period 统计时间段
     * @return 统计列表
     */
    List<Map> getJobExecutionStatusCount(@NonNull Long[] period);

    /**
     * 统计正在运行的flow
     *
     * @param filterSource         需要过滤的已接入系统
     * @param filterScheduleStatus 需要过滤的调度状态
     * @return 总数
     */
    int getRunningFlowCount(@Nullable String[] filterSource, @Nullable String[] filterScheduleStatus);

    /**
     * 统计正在运行的job
     *
     * @param filterSource         需要过滤的已接入系统
     * @param filterScheduleStatus 需要过滤的调度状态
     * @return 总数
     */
    int getRunningJobCount(@Nullable String[] filterSource, @Nullable String[] filterScheduleStatus);

    /**
     * 统计增长的flow
     *
     * @param period 统计时间段
     * @return 总数
     */
    int getIncreaseFlowCount(@NonNull Long[] period);

    /**
     * 统计增长的job
     *
     * @param period 统计时间段
     * @return 总数
     */
    int getIncreaseJobCount(@NonNull Long[] period);

    /**
     * 统计历史执行情况
     *
     * @param period   统计时间段
     * @param pointNum 折线图的点数
     * @return 统计列表
     */
    List<StatusHistoryCount> getStatusHistoryCount(@NonNull Long[] period, int pointNum);
}
