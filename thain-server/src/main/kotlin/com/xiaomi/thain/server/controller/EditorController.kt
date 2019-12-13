package com.xiaomi.thain.server.controller

import com.xiaomi.thain.common.entity.ApiResult
import com.xiaomi.thain.core.model.rq.AddFlowAndJobsRq
import com.xiaomi.thain.server.service.CheckService
import com.xiaomi.thain.server.service.FlowService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.javaClass

/**
 * @author liangyongrui@xiaomi.com
 */
@RestController
@RequestMapping("api/editor")
class EditorController(private val flowService: FlowService,
                       private val checkService: CheckService) {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @PostMapping("")
    fun addFlow(@RequestBody json: String): ApiResult {
        return try {
            val gson = com.google.gson.Gson()
            val addRq = gson.fromJson(json, AddFlowAndJobsRq::class.java).copy()
            add(addRq.copy(flowModel = addRq.flowModel.copy(createUser = com.xiaomi.thain.server.handler.ThreadLocalUser.getUsername())), "thain")
        } catch (e: Exception) {
            log.error("", e)
            ApiResult.fail(e.message)
        }
    }

    fun add(addRq: AddFlowAndJobsRq, appId: String): ApiResult {
        val addFlowRq = addRq.flowModel
        val jobModelList = addRq.jobModelList
        try {
            checkService.checkFlowModel(addFlowRq)
            checkService.checkJobModelList(jobModelList)
        } catch (e: Exception) {
            return ApiResult.fail(e.message)
        }
        return try {
            ApiResult.success(flowService.add(addFlowRq, jobModelList, appId))
        } catch (e: Exception) {
            log.error("add", e)
            ApiResult.fail(e.message)
        }
    }

}
