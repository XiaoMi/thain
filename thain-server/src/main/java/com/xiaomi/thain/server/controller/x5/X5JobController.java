package com.xiaomi.thain.server.controller.x5;


import com.alibaba.fastjson.JSON;
import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.common.model.rq.UpdateJobPropertiesRq;
import com.xiaomi.thain.server.service.JobService;
import com.xiaomi.thain.server.service.PermissionService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("x5/job")
public class X5JobController {

    @NonNull
    private final PermissionService permissionService;

    @NonNull
    private final JobService jobService;

    public X5JobController(@NonNull PermissionService permissionService,
                           @NonNull JobService jobService) {
        this.permissionService = permissionService;
        this.jobService = jobService;
    }


    @PostMapping("update-properties")
    public ApiResult updateJobProperties(@NonNull @RequestBody String json, @NonNull String appId) {
        try {
            val updateJobPropertiesRq = JSON.parseObject(json, UpdateJobPropertiesRq.class);
            if (!permissionService.getFlowAccessible(updateJobPropertiesRq.flowId, appId)) {
                return ApiResult.forbidden();
            }
            jobService.updateJobProperties(updateJobPropertiesRq);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("add:", e);
            return ApiResult.fail(e.getMessage());
        }
    }

}
