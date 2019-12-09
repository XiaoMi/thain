/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.controller.x5;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.common.exception.ThainFlowRunningException;
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException;
import com.xiaomi.thain.common.model.rq.kt.AddFlowAndJobsRq;
import com.xiaomi.thain.server.controller.FlowController;
import com.xiaomi.thain.server.service.FlowService;
import com.xiaomi.thain.server.service.PermissionService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author miaoyu
 * @date 19-7-8 下午2:51
 */
@Slf4j
@RestController
@RequestMapping("x5/flow")
public class X5FlowController {
    private static final String NO_PERMISSION_MESSAGE = "You do not have permission to do this operation";
    private static final String FLOW_ID = "flowId";
    @NonNull
    private final FlowService flowService;
    @NonNull
    private final PermissionService permissionService;
    @NonNull
    private final FlowController flowController;

    public X5FlowController(@NonNull FlowService flowService,
                            @NonNull PermissionService permissionService,
                            @NonNull FlowController flowController) {
        this.flowService = flowService;
        this.permissionService = permissionService;
        this.flowController = flowController;
    }

    @PostMapping("add")
    public ApiResult add(@NonNull @RequestBody String json, @NonNull String appId) {
        try {
            Gson gson = new Gson();
            val addRq = gson.fromJson(json, AddFlowAndJobsRq.class);
            //todo
//            return flowController.add(addRq, appId);
            return null;
        } catch (Exception e) {
            log.error("add:", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @PostMapping("delete")
    public ApiResult delete(@NonNull @RequestBody String json, @NonNull String appId) {
        try {
            Long flowId = JSON.parseObject(json).getLong(FLOW_ID);
            if (!permissionService.getFlowAccessible(flowId, appId)) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            flowService.delete(flowId);
        } catch (Exception e) {
            log.error("delete:", e);
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success();
    }

    @PostMapping("start")
    public ApiResult start(@NonNull @RequestBody String json, @NonNull String appId) {
        try {
            Long flowId = JSON.parseObject(json).getLong(FLOW_ID);
            if (!permissionService.getFlowAccessible(flowId, appId)) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            return ApiResult.success(flowService.start(flowId));
        } catch (ThainRepeatExecutionException | ThainFlowRunningException e) {
            log.warn(ExceptionUtils.getRootCauseMessage(e));
            return ApiResult.fail(e.getMessage());
        } catch (Exception e) {
            log.error("start:", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @PostMapping("pause")
    public ApiResult pause(@NonNull @RequestBody String json, @NonNull String appId) {
        try {
            Long flowId = JSON.parseObject(json).getLong(FLOW_ID);
            if (!permissionService.getFlowAccessible(flowId, appId)) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            flowService.pause(flowId);
        } catch (Exception e) {
            log.error("pause:", e);
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success();
    }

    @PostMapping("schedule")
    public ApiResult schedule(@NonNull @RequestBody String json, @NonNull String appId) {
        try {
            Long flowId = JSON.parseObject(json).getLong(FLOW_ID);
            String cron = JSON.parseObject(json).getString("cron");
            if (!permissionService.getFlowAccessible(flowId, appId)) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE);
            }
            flowService.updateCron(flowId, cron);
        } catch (Exception e) {
            log.error("scheduling:", e);
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success();
    }
}
