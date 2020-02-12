package com.xiaomi.thain.server.service

import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException
import com.xiaomi.thain.common.model.ComponentDefine
import com.xiaomi.thain.core.model.dr.FlowDr
import com.xiaomi.thain.core.model.dr.JobDr
import com.xiaomi.thain.core.model.rq.AddFlowAndJobsRq
import com.xiaomi.thain.core.model.rq.AddFlowRq
import com.xiaomi.thain.core.model.rq.AddJobRq
import com.xiaomi.thain.core.model.rq.UpdateFlowRq
import com.xiaomi.thain.common.utils.ifNull
import com.xiaomi.thain.core.ThainFacade
import com.xiaomi.thain.server.dao.FlowDao
import com.xiaomi.thain.server.model.sp.FlowListSp
import org.quartz.SchedulerException
import org.springframework.stereotype.Service
import java.io.IOException
import java.text.ParseException

/**
 * @author liangyongrui
 */
@Service
class FlowService(
        private val flowDao: FlowDao,
        private val thainFacade: ThainFacade) {

    fun getFlowList(flowListSp: FlowListSp): List<FlowDr> {
        return flowDao.getFlowList(flowListSp)
    }

    fun getFlowListCount(flowListSp: FlowListSp): Long {
        return flowDao.getFlowListCount(flowListSp)
    }

    fun add(addFlowRq: AddFlowRq, addJobRqList: List<AddJobRq>, appId: String): Long {
        val flow = addFlowRq
                .takeIf { !it.slaKill || it.slaDuration == 0L }
                ?.copy(slaKill = true, slaDuration = 3L * 60 * 60)
                .ifNull { addFlowRq }.copy(createAppId = appId)
        val flowId = flow.id
        if (flowId != null && flowDao.flowExist(flowId)) {
            val updateFlowRq = UpdateFlowRq(flow, flowId)
            thainFacade.updateFlow(updateFlowRq, addJobRqList)
            return updateFlowRq.id
        }
        return thainFacade.addFlow(AddFlowAndJobsRq(flow, addJobRqList))
                .also { flowDao.updateAppId(it, appId) }
    }

    @Throws(SchedulerException::class)
    fun delete(flowId: Long, appId: String, username: String): Boolean {
        thainFacade.deleteFlow(flowId, appId, username)
        return true
    }

    fun start(flowId: Long, variables: Map<String, String>, appId: String, username: String): Long {
        return thainFacade.startFlow(flowId, variables, appId, username)
    }

    fun getFlow(flowId: Long): FlowDr? {
        return flowDao.getFlow(flowId)
    }

    fun getJobModelList(flowId: Long): List<JobDr> {
        return flowDao.getJobModelList(flowId)
    }

    fun getComponentDefine(): List<ComponentDefine> {
        return thainFacade.componentService.componentDefineList
    }

    @Throws(ThainException::class, SchedulerException::class, IOException::class)
    fun scheduling(flowId: Long, appId: String, username: String) {
        thainFacade.schedulingFlow(flowId, appId, username)
    }

    @Throws(ThainException::class, ParseException::class, SchedulerException::class, IOException::class)
    fun updateCron(flowId: Long, cron: String?) {
        thainFacade.updateCron(flowId, cron)
    }

    @Throws(ThainException::class)
    fun pause(flowId: Long, appId: String, username: String) {
        thainFacade.pauseFlow(flowId, appId, username, false)
    }

}
