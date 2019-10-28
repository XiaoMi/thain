/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.controller;

import com.google.gson.Gson;
import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.common.exception.ThainFlowRunningException;
import com.xiaomi.thain.common.model.dto.AddDto;
import com.xiaomi.thain.server.entity.query.FlowAllInfoQuery;
import com.xiaomi.thain.server.entity.query.FlowListQuery;
import com.xiaomi.thain.server.entity.request.FlowListRequest;
import com.xiaomi.thain.server.service.CheckService;
import com.xiaomi.thain.server.service.FlowService;
import com.xiaomi.thain.server.service.PermissionService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.*;

import static com.xiaomi.thain.server.handler.ThreadLocalUser.*;

/**
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
@RestController
@RequestMapping("api/flow")
public class FlowController {

    private static final String NO_PERMISSION_MESSAGE = "You do not have permission to do this operation";

    @NonNull
    private final FlowService flowService;
    @NonNull
    private final CheckService checkService;
    @NonNull
    private final PermissionService permissionService;

    public FlowController(@NonNull FlowService flowService,
                          @NonNull CheckService checkService,
                          @NonNull PermissionService permissionService) {
        this.flowService = flowService;
        this.checkService = checkService;
        this.permissionService = permissionService;
    }

    @GetMapping("/getComponentDefineJson")
    public ApiResult getComponentDefineJson() {
        try {
            return ApiResult.success(flowService.getComponentDefineStringMap());
        } catch (Exception e) {
            log.error("getComponentDefineJson", e);
            return ApiResult.fail("Failed to obtain frontend component json definition : " + e.getMessage());
        }
    }

    @GetMapping("list")
    public ApiResult list(@NonNull FlowListRequest flowListRequest) {
        try {
            FlowListQuery flowListQuery;
            if (isAdmin()) {
                flowListQuery = FlowListQuery.getInstance(flowListRequest);
            } else {
                flowListQuery = FlowListQuery.getInstance(flowListRequest, getUsername(), getAuthorities());
            }
            return ApiResult.success(
                    flowService.getFlowList(flowListQuery),
                    flowService.getFlowListCount(flowListQuery),
                    flowListRequest.page == null ? 1 : flowListRequest.page,
                    flowListRequest.pageSize == null ? 20 : flowListRequest.pageSize);
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
    }

    @GetMapping("all-info/{flowId}")
    public ApiResult getAllInfo(@PathVariable("flowId") long flowId) {
        try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            val flowModel = flowService.getFlow(flowId);
            val jobModelList = flowService.getJobModelList(flowId);
            return ApiResult.success(FlowAllInfoQuery.builder()
                    .flowModel(flowModel)
                    .jobModelList(jobModelList).build());
        } catch (Exception e) {
            return ApiResult.fail(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @PostMapping("add")
    public ApiResult add(@NonNull @RequestBody String json) {
        try {
            Gson gson = new Gson();
            val flowDefinition = gson.fromJson(json, AddDto.class);
            return add(flowDefinition
                    .toBuilder()
                    .flowModel(flowDefinition.flowModel
                            .toBuilder()
                            .createUser(getUsername())
                            .build())
                    .build(), "thain");
        } catch (Exception e) {
            log.error("", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    public ApiResult add(@NonNull AddDto addDto, @NonNull String appId) {
        val flowModel = addDto.flowModel;
        val jobModelList = addDto.jobModelList;
        try {
            checkService.checkFlowModel(flowModel);
            checkService.checkJobModelList(jobModelList);
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
        try {
            return ApiResult.success(flowService.add(flowModel, jobModelList, appId));
        } catch (Exception e) {
            log.error("add", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @DeleteMapping("{flowId}")
    public ApiResult delete(@PathVariable long flowId) {
        try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            flowService.delete(flowId);
        } catch (Exception e) {
            log.error("delete:", e);
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success();
    }

    @PatchMapping("/start/{flowId}")
    public ApiResult start(@PathVariable long flowId) {
        try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            flowService.start(flowId);
        } catch (ThainFlowRunningException e) {
            log.warn(ExceptionUtils.getRootCauseMessage(e));
            return ApiResult.fail(e.getMessage());
        } catch (Exception e) {
            log.error("start:", e);
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success();
    }

    @PatchMapping("/scheduling/{flowId}")
    public ApiResult scheduling(@PathVariable long flowId) {
        try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            flowService.scheduling(flowId);
        } catch (Exception e) {
            log.error("scheduling:", e);
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success();
    }

    @PatchMapping("/update/{flowId}/{cron}")
    public ApiResult updateCron(@PathVariable long flowId, @NonNull @PathVariable String cron) {
        try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            flowService.updateCron(flowId, cron);
        } catch (Exception e) {
            log.error("scheduling:", e);
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success();
    }

    @PatchMapping("/pause/{flowId}")
    public ApiResult pause(@PathVariable long flowId) {
        try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            flowService.pause(flowId);
        } catch (Exception e) {
            log.error("pause:", e);
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success();
    }
}
