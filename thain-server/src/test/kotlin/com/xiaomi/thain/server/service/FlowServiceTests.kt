/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service

import com.alibaba.fastjson.JSON
import com.xiaomi.thain.common.constant.FlowSchedulingStatus
import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException
import com.xiaomi.thain.common.model.FlowModel
import com.xiaomi.thain.common.model.JobModel
import com.xiaomi.thain.common.model.rq.AddFlowRq
import com.xiaomi.thain.common.model.rq.AddJobRq
import com.xiaomi.thain.server.Application
import com.xiaomi.thain.server.service.impl.FlowServiceImpl
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.quartz.SchedulerException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.io.IOException
import java.text.ParseException
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class])
class FlowServiceTests {
    @Autowired
    private val flowService: FlowServiceImpl? = null

    @Test
    @Throws(ParseException::class, ThainException::class, SchedulerException::class, InterruptedException::class, ThainRepeatExecutionException::class, IOException::class)
    fun test() {
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
        val flow = flowService.getFlow(flowId)
        Assert.assertEquals(flow.schedulingStatus.toLong(), FlowSchedulingStatus.SCHEDULING.code.toLong())
        val flowId2 = flowService.add(addFlowRq.copy(id = flowId), jobs, "thain");
        Assert.assertEquals(flowId, flowId2);
        val flow2 = flowService.getFlow(flowId)
        Assert.assertEquals(flow2.schedulingStatus.toLong(), FlowSchedulingStatus.SCHEDULING.code.toLong())
        flowService.pause(flowId)
        flowService.scheduling(flowId)
        flowService.delete(flowId)
    }
}
