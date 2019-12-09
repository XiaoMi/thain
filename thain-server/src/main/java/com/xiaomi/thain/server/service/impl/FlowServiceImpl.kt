/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service.impl

import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException
import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.common.model.FlowModel
import com.xiaomi.thain.common.model.JobModel
import com.xiaomi.thain.core.ThainFacade
import com.xiaomi.thain.server.dao.FlowDao
import com.xiaomi.thain.common.model.rq.kt.AddFlowAndJobsRq
import com.xiaomi.thain.common.model.rq.kt.AddFlowRq
import com.xiaomi.thain.common.model.rq.kt.AddJobRq
import com.xiaomi.thain.server.model.sp.FlowListSp
import com.xiaomi.thain.server.service.FlowService
import lombok.extern.slf4j.Slf4j
import org.quartz.SchedulerException
import org.springframework.stereotype.Service
import java.io.IOException
import java.text.ParseException

/**
 * @author liangyongrui
 */
@Service
@Slf4j
class FlowServiceImpl(
        private val flowDao: FlowDao,
        private val thainFacade: ThainFacade) : FlowService {

    override fun getFlowList(flowListSp: FlowListSp): List<FlowModel> {
        return flowDao.getFlowList(flowListSp)
    }

    override fun getFlowListCount(flowListSp: FlowListSp): Long {
        return flowDao.getFlowListCount(flowListSp)
    }

    @Throws(ThainException::class, ParseException::class, SchedulerException::class)
    override fun add(addFlowRq: AddFlowRq, addJobRqList: List<AddJobRq>, appId: String): Long {
        var localAddFlowRq = addFlowRq
        if (!localAddFlowRq.slaKill || localAddFlowRq.slaDuration == 0L) {
            localAddFlowRq = localAddFlowRq.copy(
                    slaKill = true,
                    slaDuration = 3L * 60 * 60
            )
        }
        //todo update
//        if (localAddFlowRq.id != null && flowDao.flowExist(localAddFlowRq.id!!)) {
//            val updateFlowRq = UpdateFlowRq.getInstance(localAddFlowRq, localAddFlowRq.id!!)
//            thainFacade.updateFlow(updateFlowRq, jobModelList)
//            return updateFlowRq.id
//        }
        val flowId = thainFacade.addFlow(AddFlowAndJobsRq(localAddFlowRq, addJobRqList))
        flowDao.updateAppId(flowId, appId)
        return flowId
    }

    @Throws(SchedulerException::class)
    override fun delete(flowId: Long): Boolean {
        thainFacade.deleteFlow(flowId)
        return true
    }

    @Throws(ThainException::class, ThainRepeatExecutionException::class)
    override fun start(flowId: Long): Long {
        return thainFacade.startFlow(flowId)
    }

    override fun getFlow(flowId: Long): FlowModel {
        return flowDao.getFlow(flowId).orElseThrow { ThainRuntimeException("Flow does not exist, flow Id:$flowId") }
    }

    override fun getJobModelList(flowId: Long): List<JobModel> {
        return flowDao.getJobModelList(flowId)
    }

    override fun getComponentDefineStringMap(): Map<String, String> {
        return thainFacade.componentDefineJsonList
    }

    @Throws(ThainException::class, SchedulerException::class, IOException::class)
    override fun scheduling(flowId: Long) {
        thainFacade.schedulingFlow(flowId)
    }

    @Throws(ThainException::class, ParseException::class, SchedulerException::class, IOException::class)
    override fun updateCron(flowId: Long, cron: String?) {
        thainFacade.updateCron(flowId, cron)
    }

    @Throws(ThainException::class)
    override fun pause(flowId: Long) {
        thainFacade.pauseFlow(flowId)
    }

}
