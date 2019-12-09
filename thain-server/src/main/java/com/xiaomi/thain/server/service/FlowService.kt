/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service

import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException
import com.xiaomi.thain.common.model.FlowModel
import com.xiaomi.thain.common.model.JobModel
import com.xiaomi.thain.common.model.rq.kt.AddFlowRq
import com.xiaomi.thain.common.model.rq.kt.AddJobRq
import com.xiaomi.thain.server.model.sp.FlowListSp
import org.quartz.SchedulerException
import org.springframework.stereotype.Service
import java.io.IOException
import java.text.ParseException

/**
 * @author liangyongrui
 */
@Service
interface FlowService {
    fun getFlowList(flowListSp: FlowListSp): List<FlowModel?>
    fun getFlowListCount(flowListSp: FlowListSp): Long
    /**
     * 创建或更新任务
     */
    @Throws(ThainException::class, ParseException::class, SchedulerException::class)
    fun add(addFlowRq: AddFlowRq, addJobRqList: List<AddJobRq>, appId: String): Long

    /**
     * 删除
     */
    @Throws(SchedulerException::class)
    fun delete(flowId: Long): Boolean

    /**
     * 立即执行一次, 返回flow execution id
     */
    @Throws(ThainException::class, ThainRepeatExecutionException::class)
    fun start(flowId: Long): Long

    fun getFlow(flowId: Long): FlowModel
    fun getJobModelList(flowId: Long): List<JobModel>
    fun getComponentDefineStringMap(): Map<String, String>
    @Throws(ThainException::class, SchedulerException::class, IOException::class)
    fun scheduling(flowId: Long)

    @Throws(ThainException::class)
    fun pause(flowId: Long)

    @Throws(ThainException::class, ParseException::class, SchedulerException::class, IOException::class)
    fun updateCron(flowId: Long, cron: String?)
}
