package com.xiaomi.thain.server.controller

import com.xiaomi.thain.common.entity.ApiResult
import com.xiaomi.thain.server.handler.ThreadLocalUser
import com.xiaomi.thain.server.model.rp.FlowExecutionAllInfoRp
import com.xiaomi.thain.server.service.FlowExecutionService
import com.xiaomi.thain.server.service.PermissionService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.web.bind.annotation.*

/**
 * @author liangyongrui
 */
@RestController
@RequestMapping("api/flow-execution")
class FlowExecutionController(private val flowExecutionService: FlowExecutionService,
                              private val permissionService: PermissionService) {

    @GetMapping("list")
    fun queryFlowExecutionLogsByFlowId(flowId: Long?, page: Int?, pageSize: Int?): ApiResult {
        var pageT = page
        var pageSizeT = pageSize
        return try {
            if (pageT == null || pageT <= 0) {
                pageT = 1
            }
            if (pageSizeT == null || pageSizeT <= 0) {
                pageSizeT = 20
            }
            if (flowId == null || flowId <= 0) {
                ApiResult.success(emptyList<Any>(), 0, 1, pageSizeT)
            } else ApiResult.success(
                    flowExecutionService.getFlowExecutionList(flowId, pageT, pageSizeT),
                    flowExecutionService.getFlowExecutionCount(flowId),
                    pageT,
                    pageSizeT
            )
        } catch (e: Exception) {
            ApiResult.fail(e.message)
        }
    }

    @GetMapping("all-info/{flowExecutionId}")
    fun getAllInfo(@PathVariable("flowExecutionId") flowExecutionId: Long): ApiResult {
        return try {
            if (!ThreadLocalUser.isAdmin() && !permissionService.getFlowExecutionAccessible(flowExecutionId, ThreadLocalUser.getUsername(), ThreadLocalUser.getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            val flowExecutionModel = flowExecutionService.getFlowExecution(flowExecutionId)
            val jobExecutionModelList = flowExecutionService.getJobExecutionModelList(flowExecutionId)
            val jobModelList = flowExecutionService.getJobModelList(flowExecutionId)
            ApiResult.success(FlowExecutionAllInfoRp(
                    flowExecutionModel = flowExecutionModel,
                    jobModelList = jobModelList,
                    jobExecutionModelList = jobExecutionModelList))
        } catch (e: Exception) {
            ApiResult.fail(ExceptionUtils.getRootCauseMessage(e))
        }
    }

    @PatchMapping("kill/{flowExecutionId}")
    fun killFlowExecution(@PathVariable("flowExecutionId") flowExecutionId: Long): ApiResult {
        return try {
            if (!ThreadLocalUser.isAdmin() && !permissionService.getFlowExecutionAccessible(flowExecutionId, ThreadLocalUser.getUsername(), ThreadLocalUser.getAuthorities())) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            val (_, flowId) = flowExecutionService.getFlowExecution(flowExecutionId)
            flowExecutionService.killFlowExecution(flowId, flowExecutionId, "thain", ThreadLocalUser.getUsername())
            ApiResult.success()
        } catch (e: Exception) {
            ApiResult.fail(e.message)
        }
    }

    @GetMapping("{flowExecutionId}")
    fun getFlowExecution(@PathVariable("flowExecutionId") flowExecutionId: Long): ApiResult {
        return try {
            if (!ThreadLocalUser.isAdmin() && !permissionService.getFlowExecutionAccessible(flowExecutionId, ThreadLocalUser.getUsername(), ThreadLocalUser.getAuthorities())) {
                ApiResult.fail(NO_PERMISSION_MESSAGE)
            } else ApiResult.success(flowExecutionService.getFlowExecution(flowExecutionId))
        } catch (e: Exception) {
            ApiResult.fail(e.message)
        }
    }

    companion object {
        private const val NO_PERMISSION_MESSAGE = "You do not have permission to do this operation"
    }

}
