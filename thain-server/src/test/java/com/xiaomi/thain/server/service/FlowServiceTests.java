/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.xiaomi.thain.common.constant.FlowSchedulingStatus;
import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.common.model.rq.AddFlowRq;
import com.xiaomi.thain.server.Application;
import com.xiaomi.thain.server.service.impl.FlowServiceImpl;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class FlowServiceTests {

    @Autowired
    private FlowServiceImpl flowService;

    @Test
    public void test() throws ParseException, ThainException, SchedulerException, InterruptedException {
        val addFlowRq = AddFlowRq.builder()
                .name("test")
                .cron("* * * * * ?")
                .createUser("admin")
                .build();
        val jobs = ImmutableList.of(JobModel.builder()
                .name("test")
                .component("std::http")
                .properties(ImmutableMap.of(
                        "method", "GET",
                        "contentType", "application/json",
                        "url", "https://github.com"
                )).build());
        val flowId = flowService.add(addFlowRq, jobs, "thain");
        TimeUnit.SECONDS.sleep(5);
        val flow = flowService.getFlow(flowId);
        Assert.assertEquals(flow.schedulingStatus, FlowSchedulingStatus.SCHEDULING.code);
        val flowId2 = flowService.add(addFlowRq.toBuilder().id(flowId).build(), jobs, "thain");
        Assert.assertEquals(flowId, flowId2);
        Assert.assertEquals(flow.schedulingStatus, FlowSchedulingStatus.SCHEDULING.code);
    }

}
