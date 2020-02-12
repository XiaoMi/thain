/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.controller

import com.xiaomi.thain.common.entity.ApiResult
import com.xiaomi.thain.common.exception.ThainFlowRunningException
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException
import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.server.handler.ThreadLocalUser.*
import com.xiaomi.thain.server.model.rp.FlowAllInfoRp
import com.xiaomi.thain.server.model.rq.FlowListRq
import com.xiaomi.thain.server.model.sp.FlowListSp
import com.xiaomi.thain.server.service.FlowExecutionService
import com.xiaomi.thain.server.service.FlowService
import com.xiaomi.thain.server.service.PermissionService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

/**
 * @author liangyongrui@xiaomi.com
 */
@RestController
@RequestMapping("api/flow")
class FlowController(private val flowService: FlowService,
                     private val permissionService: PermissionService,
                     private val flowExecutionService: FlowExecutionService) {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @GetMapping("/getComponentDefineJson")
    fun getComponentDefineJson(): ApiResult {
        return try {
            ApiResult.success(flowService.getComponentDefine())
        } catch (e: Exception) {
            log.error("getComponentDefineJson", e)
            ApiResult.fail("Failed to obtain frontend component json definition : " + e.message)
        }
    }

    @GetMapping("list")
    fun list(flowListRq: FlowListRq): ApiResult {
        return try {
            val flowListSp: FlowListSp = if (isAdmin()) {
                FlowListSp.getInstance(flowListRq)
            } else {
                FlowListSp.getInstance(flowListRq, getUsername(), getAuthorities())
            }
            ApiResult.success(
                    flowService.getFlowList(flowListSp),
                    flowService.getFlowListCount(flowListSp),
                    flowListRq.page ?: 1,
                    flowListRq.pageSize ?: 20)
        } catch (e: Exception) {
            ApiResult.fail(e.message)
        }
    }

    @GetMapping("all-info/{flowId}")
    fun getAllInfo(@PathVariable("flowId") flowId: Long): ApiResult {
        return try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            val flowModel = flowService.getFlow(flowId) ?: throw ThainRuntimeException()
            val jobModelList = flowService.getJobModelList(flowId)
            ApiResult.success(FlowAllInfoRp(flowModel, jobModelList))
        } catch (e: Exception) {
            ApiResult.fail(ExceptionUtils.getRootCauseMessage(e))
        }
    }

    @DeleteMapping("{flowId}")
    fun delete(@PathVariable flowId: Long): ApiResult {
        try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            flowService.delete(flowId, DEFAULT_APP_ID, getUsername())
        } catch (e: Exception) {
            log.error("delete:", e)
            return ApiResult.fail(e.message)
        }
        return ApiResult.success()
    }

    @PatchMapping("/start/{flowId}")
    fun start(@PathVariable flowId: Long, @RequestBody variables: Map<String, String>): ApiResult {
        return try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                ApiResult.fail(NO_PERMISSION_MESSAGE)
            } else {
                ApiResult.success(flowService.start(flowId,variables, DEFAULT_APP_ID, getUsername()))
            }
        } catch (e: ThainRepeatExecutionException) {
            log.warn(ExceptionUtils.getRootCauseMessage(e))
            ApiResult.fail(e.message)
        } catch (e: ThainFlowRunningException) {
            log.warn(ExceptionUtils.getRootCauseMessage(e))
            ApiResult.fail(e.message)
        } catch (e: Exception) {
            log.error("start:", e)
            ApiResult.fail(e.message)
        }
    }

    @PatchMapping("/scheduling/{flowId}")
    fun scheduling(@PathVariable flowId: Long): ApiResult {
        try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            flowService.scheduling(flowId, DEFAULT_APP_ID, getUsername())
        } catch (e: Exception) {
            log.error("scheduling:", e)
            return ApiResult.fail(e.message)
        }
        return ApiResult.success()
    }

    @PatchMapping("/update/{flowId}/{cron}")
    fun updateCron(@PathVariable flowId: Long, @PathVariable cron: String): ApiResult {
        try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            flowService.updateCron(flowId, cron)
        } catch (e: Exception) {
            log.error("scheduling:", e)
            return ApiResult.fail(e.message)
        }
        return ApiResult.success()
    }

    @PatchMapping("/pause/{flowId}")
    fun pause(@PathVariable flowId: Long): ApiResult {
        try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            flowService.pause(flowId, DEFAULT_APP_ID, getUsername())
        } catch (e: Exception) {
            log.error("pause:", e)
            return ApiResult.fail(e.message)
        }
        return ApiResult.success()
    }

    @PatchMapping("/kill/{flowId}")
    fun kill(@PathVariable flowId: Long): ApiResult {
        try {
            if (!isAdmin() && !permissionService.getFlowAccessible(flowId, getUsername(), getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            if (!flowExecutionService.killFlowExecutionsByFlowId(flowId, DEFAULT_APP_ID, getUsername())) {
                return ApiResult.fail("No execution need kill")
            }
        } catch (e: Exception) {
            log.error("kill:", e)
            return ApiResult.fail(e.message)
        }
        return ApiResult.success()
    }

    companion object {
        private const val NO_PERMISSION_MESSAGE = "You do not have permission to do this operation"
        private const val DEFAULT_APP_ID = "thain"
    }

}
