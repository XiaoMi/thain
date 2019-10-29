/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.controller.x5;

import com.alibaba.fastjson.JSON;
import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.server.model.rp.FlowExecutionAllInfoRp;
import com.xiaomi.thain.server.service.FlowExecutionService;
import com.xiaomi.thain.server.service.PermissionService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author miaoyu
 * @date 19-7-8 下午2:57
 */
@Slf4j
@RestController
@RequestMapping("x5/flow-execution")
public class X5FlowExecutionController {
    private static final String NO_PERMISSION_MESSAGE = "You do not have permission to do this operation";
    @NonNull
    private final FlowExecutionService flowExecutionService;
    @NonNull
    private final PermissionService permissionService;

    public X5FlowExecutionController(@NonNull FlowExecutionService flowExecutionService, @NonNull PermissionService permissionService) {
        this.flowExecutionService = flowExecutionService;
        this.permissionService = permissionService;
    }

    @PostMapping("all-info")
    public ApiResult getAllInfo(@NonNull String json, @NonNull String appId) {
        try {
            Long flowExecutionId = JSON.parseObject(json).getLong("flowExecutionId");
            if (!permissionService.getFlowExecutionAccessible(flowExecutionId, appId)) {
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

    @PostMapping("kill")
    public ApiResult killFlowExecution(@NonNull String json, @NonNull String appId) {
        try {
            Long flowExecutionId = JSON.parseObject(json).getLong("flowExecutionId");
            if (!permissionService.getFlowExecutionAccessible(flowExecutionId, appId)) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            flowExecutionService.killFlowExecution(flowExecutionId);
            return ApiResult.success();
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
    }

    @PostMapping("infos")
    public ApiResult getInfos(@NonNull String json, @NonNull String appId) {
        Long flowId = JSON.parseObject(json).getLong("flowId");
        if (permissionService.getFlowAccessible(flowId, appId)){
            int page = JSON.parseObject(json).getInteger("page");
            int pageSize = JSON.parseObject(json).getInteger("pageSize");
            if (page<1){
                page=1;
            }
            if (pageSize<1){
                pageSize=10;
            }
            return ApiResult.success(flowExecutionService.getFlowExecutionList(flowId, page, pageSize),
                    flowExecutionService.getFlowExecutionCount(flowId),
                    page,
                    pageSize);
        }
        return ApiResult.fail(NO_PERMISSION_MESSAGE);
    }
}
