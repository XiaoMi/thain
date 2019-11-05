/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.controller;

import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.server.model.rp.FlowExecutionAllInfoRp;
import com.xiaomi.thain.server.service.FlowExecutionService;
import com.xiaomi.thain.server.service.PermissionService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;

import static com.xiaomi.thain.server.handler.ThreadLocalUser.*;

/**
 * @author liangyongrui
 */
@Log4j2
@RestController
@RequestMapping("api/flow-execution")
public class FlowExecutionController {
    private static final String NO_PERMISSION_MESSAGE = "You do not have permission to do this operation";
    @NonNull
    private final FlowExecutionService flowExecutionService;
    @NonNull
    private final PermissionService permissionService;

    public FlowExecutionController(@NonNull FlowExecutionService flowExecutionService, @NonNull PermissionService permissionService) {
        this.flowExecutionService = flowExecutionService;
        this.permissionService = permissionService;
    }

    @GetMapping("list")
    public ApiResult queryFlowExecutionLogsByFlowId(@Nullable Long flowId, @Nullable Integer page, @Nullable Integer pageSize) {
        try {
            if (Objects.isNull(page) || page <= 0) {
                page = 1;
            }
            if (Objects.isNull(pageSize) || pageSize <= 0) {
                pageSize = 20;
            }
            if (Objects.isNull(flowId) || flowId <= 0) {
                return ApiResult.success(
                        Collections.emptyList(), 0, 1, pageSize
                );
            }
            return ApiResult.success(
                    flowExecutionService.getFlowExecutionList(flowId, page, pageSize),
                    flowExecutionService.getFlowExecutionCount(flowId),
                    page,
                    pageSize
            );
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
    }

    @GetMapping("all-info/{flowExecutionId}")
    public ApiResult getAllInfo(@PathVariable("flowExecutionId") long flowExecutionId) {
        try {
            if (!isAdmin() && !permissionService.getFlowExecutionAccessible(flowExecutionId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            val flowExecutionModel = flowExecutionService.getFlowExecution(flowExecutionId);
            val jobExecutionModelList = flowExecutionService.getJobExecutionModelList(flowExecutionId);
            val jobModelList = flowExecutionService.getJobModelList(flowExecutionId);
            return ApiResult.success(FlowExecutionAllInfoRp.builder()
                    .flowExecutionModel(flowExecutionModel)
                    .jobModelList(jobModelList)
                    .jobExecutionModelList(jobExecutionModelList)
                    .build());
        } catch (Exception e) {
            return ApiResult.fail(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @PatchMapping("kill/{flowExecutionId}")
    public ApiResult killFlowExecution(@PathVariable("flowExecutionId") long flowExecutionId) {
        try {
            if (!isAdmin() && !permissionService.getFlowExecutionAccessible(flowExecutionId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            flowExecutionService.killFlowExecution(flowExecutionId);
            return ApiResult.success();
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
    }

    @GetMapping("{flowExecutionId}")
    public ApiResult getFlowExecution(@PathVariable("flowExecutionId") long flowExecutionId) {
        try {
            if (!isAdmin() && !permissionService.getFlowExecutionAccessible(flowExecutionId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            return ApiResult.success(flowExecutionService.getFlowExecution(flowExecutionId));
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
    }

}
