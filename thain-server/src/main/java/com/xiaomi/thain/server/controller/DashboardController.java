/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.controller;

import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.server.service.DashboardService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;

/**
 * @author miaoyu3@xiaomi.com
 */
@Slf4j
@RestController
@RequestMapping("api/dashboard")
public class DashboardController {

    @NonNull
    private final DashboardService dashboardService;

    public DashboardController(@NonNull DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("schedule-status-count")
    public ApiResult getScheduleStatusCount(@Nullable String[] filterSource) {
        try {
            return ApiResult.success(dashboardService.getScheduleStatusCount(filterSource));
        } catch (Exception e) {
            log.error("getScheduleStatusCount", e);
            return ApiResult.fail("Failed to obtain statistical results of tasks in different scheduling States: " + e.getMessage());
        }
    }

    @GetMapping("flow-source-count")
    public ApiResult getFlowSourceCount(@Nullable String[] filterScheduleStatus) {
        try {
            return ApiResult.success(dashboardService.getFlowSourceCount(filterScheduleStatus));
        } catch (Exception e) {
            log.error("getFlowSourceCount", e);
            return ApiResult.fail("Failed to obtain statistical results of tasks of different source: " + e.getMessage());
        }
    }

    @GetMapping("flow-execution-status-count")
    public ApiResult getFlowExecutionStatusCount(@NonNull Long[] period) {
        try {
            return ApiResult.success(dashboardService.getFlowExecutionStatusCount(period));
        } catch (Exception e) {
            log.error("getFlowExecutionStatusCount", e);
            return ApiResult.fail("Failed to obtain statistical results of different flow running status of days: " + e.getMessage());
        }
    }

    @GetMapping("job-execution-status-count")
    public ApiResult getJobExecutionStatusCount(@NonNull Long[] period) {
        try {
            return ApiResult.success(dashboardService.getJobExecutionStatusCount(period));
        } catch (Exception e) {
            log.error("getJobExecutionStatusCount", e);
            return ApiResult.fail("Failed to obtain statistical results of different job running status of days: " + e.getMessage());
        }
    }

    @GetMapping("running-flow-count")
    public ApiResult getRunningFlowCount(@Nullable String[] filterSource, @Nullable String[] filterScheduleStatus) {
        try {
            return ApiResult.success(dashboardService.getRunningFlowCount(filterSource, filterScheduleStatus));
        } catch (Exception e) {
            log.error("getJobExecutionStatusCount", e);
            return ApiResult.fail("Failed to obtain statistical results of running flow: " + e.getMessage());
        }
    }

    @GetMapping("running-job-count")
    public ApiResult getRunningJobCount(@Nullable String[] filterSource, @Nullable String[] filterScheduleStatus) {
        try {
            return ApiResult.success(dashboardService.getRunningJobCount(filterSource, filterScheduleStatus));
        } catch (Exception e) {
            log.error("getRunningJobCount", e);
            return ApiResult.fail("Failed to obtain statistical results of running job: " + e.getMessage());
        }
    }

    @GetMapping("increase-flow-count")
    public ApiResult getIncreaseFlowCount(@NonNull Long[] period) {
        try {
            return ApiResult.success(dashboardService.getIncreaseFlowCount(period));
        } catch (Exception e) {
            log.error("getIncreaseFlowCount", e);
            return ApiResult.fail("Failed to obtain statistical results of increasing flow of days: " + e.getMessage());
        }
    }

    @GetMapping("increase-job-count")
    public ApiResult getIncreaseJobCount(@NonNull Long[] period) {
        try {
            return ApiResult.success(dashboardService.getIncreaseJobCount(period));
        } catch (Exception e) {
            log.error("getIncreaseJobCount", e);
            return ApiResult.fail("Failed to obtain statistical results of increasing job of days: " + e.getMessage());
        }
    }

    @GetMapping("status-history-count")
    public ApiResult getStatusHistoryCount(@NonNull Long[] period, int maxPointNum) {
        try {
            return ApiResult.success(dashboardService.getStatusHistoryCount(period, maxPointNum));
        } catch (Exception e) {
            log.error("getStatusHistoryCount", e);
            return ApiResult.fail("Failed to obtain statistical results of historical flow of days: " + e.getMessage());
        }
    }

}
