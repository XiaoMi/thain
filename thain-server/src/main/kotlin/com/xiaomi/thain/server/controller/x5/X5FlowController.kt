package com.xiaomi.thain.server.controller.x5

import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.xiaomi.thain.common.entity.ApiResult
import com.xiaomi.thain.common.exception.ThainFlowRunningException
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException
import com.xiaomi.thain.core.model.rq.AddFlowAndJobsRq
import com.xiaomi.thain.server.controller.EditorController
import com.xiaomi.thain.server.service.FlowService
import com.xiaomi.thain.server.service.PermissionService
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private const val NO_PERMISSION_MESSAGE = "You do not have permission to do this operation"
private const val FLOW_ID = "flowId"
private const val VARIABLES = "variables"
private const val UNKNOWN_USER = "unknown"

/**
 * Date 19-7-8 下午2:51
 */
@RestController
@RequestMapping("x5/flow")
class X5FlowController(private val flowService: FlowService,
                       private val permissionService: PermissionService,
                       private val editorController: EditorController
) {

    private val log = LoggerFactory.getLogger(this.javaClass)!!


    @PostMapping("add")
    fun add(@RequestBody json: String, appId: String): ApiResult {
        return try {
            val gson = Gson()
            val addRq = gson.fromJson(json, AddFlowAndJobsRq::class.java)
            editorController.add(addRq, appId)
        } catch (e: Exception) {
            log.error("add:", e)
            ApiResult.fail(e.message)
        }
    }

    @PostMapping("delete")
    fun delete(@RequestBody json: String, appId: String): ApiResult {
        try {
            val flowId = JSON.parseObject(json).getLong(FLOW_ID)
            if (!permissionService.getFlowAccessible(flowId, appId)) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            flowService.delete(flowId, appId, UNKNOWN_USER)
        } catch (e: Exception) {
            log.error("delete:", e)
            return ApiResult.fail(e.message)
        }
        return ApiResult.success()
    }

    @PostMapping("start")
    fun start(@RequestBody json: String, appId: String): ApiResult {
        return try {
            val jsonObject = JSON.parseObject(json)
            val flowId = jsonObject.getLong(FLOW_ID)
            val variables = jsonObject.getJSONObject(VARIABLES) ?: mapOf<String, Any>()
            if (!permissionService.getFlowAccessible(flowId, appId)) {
                ApiResult.fail(NO_PERMISSION_MESSAGE)
            } else ApiResult.success(flowService.start(flowId, variables, appId, UNKNOWN_USER))
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

    @PostMapping("pause")
    fun pause(@RequestBody json: String, appId: String): ApiResult {
        try {
            val flowId = JSON.parseObject(json).getLong(FLOW_ID)
            if (!permissionService.getFlowAccessible(flowId, appId)) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            flowService.pause(flowId, appId, UNKNOWN_USER)
        } catch (e: Exception) {
            log.error("pause:", e)
            return ApiResult.fail(e.message)
        }
        return ApiResult.success()
    }

    @PostMapping("schedule")
    fun schedule(@RequestBody json: String, appId: String): ApiResult {
        try {
            val flowId = JSON.parseObject(json).getLong(FLOW_ID)
            val cron = JSON.parseObject(json).getString("cron")
            if (!permissionService.getFlowAccessible(flowId, appId)) {
                return ApiResult.fail(NO_PERMISSION_MESSAGE)
            }
            if (StringUtils.isNotBlank(cron)) {
                flowService.updateCron(flowId, cron)
            }
            flowService.scheduling(flowId, appId, UNKNOWN_USER)
        } catch (e: Exception) {
            log.error("scheduling:", e)
            return ApiResult.fail(e.message)
        }
        return ApiResult.success()
    }

}
