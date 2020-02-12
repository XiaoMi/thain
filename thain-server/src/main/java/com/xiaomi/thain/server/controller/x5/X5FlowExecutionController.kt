/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.controller.x5

import com.alibaba.fastjson.JSON
import com.xiaomi.thain.common.entity.ApiResult
import com.xiaomi.thain.server.model.rp.FlowExecutionAllInfoRp
import com.xiaomi.thain.server.service.FlowExecutionService
import com.xiaomi.thain.server.service.PermissionService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Date 19-7-8 下午2:57
 */
@RestController
@RequestMapping("x5/flow-execution")
class X5FlowExecutionController(private val flowExecutionService: FlowExecutionService, private val permissionService: PermissionService) {
    @PostMapping("all-info")
    fun getAllInfo(json: String, appId: String): ApiResult {
        return try {
            val flowExecutionId = JSON.parseObject(json).getLong("flowExecutionId")
            if (!permissionService.getFlowExecutionAccessible(flowExecutionId, appId)) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            val flowExecutionModel = flowExecutionService.getFlowExecution(flowExecutionId)
            val jobExecutionModelList = flowExecutionService.getJobExecutionModelList(flowExecutionId)
            val jobModelList = flowExecutionService.getJobModelList(flowExecutionId)
            ApiResult.success(FlowExecutionAllInfoRp(
                    flowExecutionModel = flowExecutionModel,
                    jobModelList = jobModelList,
                    jobExecutionModelList = jobExecutionModelList)
            )
        } catch (e: Exception) {
            ApiResult.fail(ExceptionUtils.getRootCauseMessage(e))
        }
    }

    @PostMapping("kill")
    fun killFlowExecution(json: String, appId: String): ApiResult {
        return try {
            val flowExecutionId = JSON.parseObject(json).getLong("flowExecutionId")
            if (!permissionService.getFlowExecutionAccessible(flowExecutionId, appId)) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            val (_, flowId) = flowExecutionService.getFlowExecution(flowExecutionId)
            flowExecutionService.killFlowExecution(flowId, flowExecutionId, appId, "unknown")
            ApiResult.success()
        } catch (e: Exception) {
            ApiResult.fail(e.message)
        }
    }

    @PostMapping("infos")
    fun getInfos(json: String, appId: String): ApiResult {
        val flowId = JSON.parseObject(json).getLong("flowId")
        if (permissionService.getFlowAccessible(flowId, appId)) {
            var page = JSON.parseObject(json).getInteger("page")
            var pageSize = JSON.parseObject(json).getInteger("pageSize")
            if (page < 1) {
                page = 1
            }
            if (pageSize < 1) {
                pageSize = 10
            }
            return ApiResult.success(flowExecutionService.getFlowExecutionList(flowId, page, pageSize),
                    flowExecutionService.getFlowExecutionCount(flowId),
                    page,
                    pageSize)
        }
        return ApiResult.fail(NO_PERMISSION_MESSAGE)
    }

    companion object {
        private const val NO_PERMISSION_MESSAGE = "You do not have permission to do this operation"
    }

}
