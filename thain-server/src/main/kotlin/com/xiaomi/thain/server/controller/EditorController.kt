package com.xiaomi.thain.server.controller

import kotlin.jvm.javaClass

/**
 * @author liangyongrui@xiaomi.com
 */
@lombok.extern.slf4j.Slf4j
@org.springframework.web.bind.annotation.RestController
@org.springframework.web.bind.annotation.RequestMapping("api/editor")
class EditorController(private val flowService: com.xiaomi.thain.server.service.FlowService,
                       private val checkService: com.xiaomi.thain.server.service.CheckService) {

    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)!!

    @org.springframework.web.bind.annotation.PostMapping("")
    fun addFlow(@org.springframework.web.bind.annotation.RequestBody json: String): com.xiaomi.thain.common.entity.ApiResult {
        return try {
            val gson = com.google.gson.Gson()
            val addRq = gson.fromJson(json, AddFlowAndJobsRq::class.java).copy()
            add(addRq.copy(flowModel = addRq.flowModel.copy(createUser = com.xiaomi.thain.server.handler.ThreadLocalUser.getUsername())), "thain")
        } catch (e: Exception) {
            log.error("", e)
            com.xiaomi.thain.common.entity.ApiResult.fail(e.message)
        }
    }

    fun add(addRq: AddFlowAndJobsRq, appId: String): com.xiaomi.thain.common.entity.ApiResult {
        val addFlowRq = addRq.flowModel
        val jobModelList = addRq.jobModelList
        try {
            checkService.checkFlowModel(addFlowRq)
            checkService.checkJobModelList(jobModelList)
        } catch (e: Exception) {
            return com.xiaomi.thain.common.entity.ApiResult.fail(e.message)
        }
        return try {
            com.xiaomi.thain.common.entity.ApiResult.success(flowService.add(addFlowRq, jobModelList, appId))
        } catch (e: Exception) {
            log.error("add", e)
            com.xiaomi.thain.common.entity.ApiResult.fail(e.message)
        }
    }

}