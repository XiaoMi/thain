/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service

import com.alibaba.fastjson.JSON
import com.xiaomi.thain.common.constant.FlowLastRunStatus
import com.xiaomi.thain.common.constant.FlowSchedulingStatus
import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.model.FlowModel
import com.xiaomi.thain.common.model.JobModel
import com.xiaomi.thain.core.model.rq.AddFlowRq
import com.xiaomi.thain.core.model.rq.AddJobRq
import com.xiaomi.thain.server.Application
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class])
class FlowServiceTests {
    @Autowired
    private val flowService: FlowService? = null

    @Test
    fun commonTest() {
        val addFlowRq = JSON.parseObject(JSON.toJSONString(FlowModel.builder()
                .name("test")
                .cron("* * * * * ?")
                .createUser("admin")
                .build()), AddFlowRq::class.java)
        val jobs = listOf(
                JSON.parseObject(
                        JSON.toJSONString(JobModel.builder()
                                .name("test")
                                .component("std::http")
                                .properties(mapOf(
                                        "method" to "GET",
                                        "contentType" to "application/json",
                                        "url" to "https://www.mi.com"
                                )).build()), AddJobRq::class.java))
        val flowId = flowService!!.add(addFlowRq, jobs, "thain")
        TimeUnit.SECONDS.sleep(10)
        val flow = flowService.getFlow(flowId) ?: throw ThainException()
        Assert.assertEquals(FlowSchedulingStatus.SCHEDULING.code, flow.schedulingStatus)
        val flowId2 = flowService.add(addFlowRq.copy(id = flowId, cron = ""), jobs, "thain")
        Assert.assertEquals(flowId, flowId2)
        val flow2 = flowService.getFlow(flowId) ?: throw ThainException()
        Assert.assertEquals(FlowSchedulingStatus.NOT_SET.code, flow2.schedulingStatus)
        flowService.pause(flowId, "test", "test")
        flowService.scheduling(flowId, "test", "test")
        flowService.delete(flowId, "test", "test")
    }

    @Test
    fun retryTest() {
        val addFlowRq = JSON.parseObject(JSON.toJSONString(FlowModel.builder()
                .name("test")
                .createUser("admin")
                .build()), AddFlowRq::class.java)
        val jobs = listOf(
                JSON.parseObject(
                        JSON.toJSONString(JobModel.builder()
                                .name("test")
                                .component("std::http")
                                .properties(mapOf(
                                        "method" to "GET",
                                        "contentType" to "application/json",
                                        "url" to "失败"
                                )).build()), AddJobRq::class.java))
        val flowId = flowService!!.add(addFlowRq, jobs, "thain")
        flowService.start(flowId, "test", "test")
        TimeUnit.SECONDS.sleep(10)
        val flow = flowService.getFlow(flowId) ?: throw ThainException()
        Assert.assertEquals(flow.schedulingStatus, FlowSchedulingStatus.NOT_SET.code)
        Assert.assertEquals(flow.lastRunStatus, FlowLastRunStatus.ERROR.code)
        val flowId2 = flowService.add(addFlowRq.copy(id = flowId, cron = "* * * * * ?"), jobs, "thain")
        Assert.assertEquals(flowId, flowId2)
        val flow2 = flowService.getFlow(flowId) ?: throw ThainException()
        Assert.assertEquals(flow2.schedulingStatus, FlowSchedulingStatus.SCHEDULING.code)
        Assert.assertEquals(flow2.lastRunStatus, FlowLastRunStatus.ERROR.code)
        flowService.pause(flowId, "test", "test")
        flowService.scheduling(flowId, "test", "test")
        flowService.delete(flowId, "test", "test")
    }
}
