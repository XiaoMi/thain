package com.xiaomi.thain.server.service.impl

/**
 * @author liangyongrui
 */
@org.springframework.stereotype.Service
@lombok.extern.slf4j.Slf4j
class FlowServiceImpl(
        private val flowDao: com.xiaomi.thain.server.dao.FlowDao,
        private val thainFacade: ThainFacade) : FlowService {

    override fun getFlowList(flowListSp: com.xiaomi.thain.server.model.sp.FlowListSp): List<com.xiaomi.thain.common.model.FlowModel> {
        return flowDao.getFlowList(flowListSp)
    }

    override fun getFlowListCount(flowListSp: com.xiaomi.thain.server.model.sp.FlowListSp): Long {
        return flowDao.getFlowListCount(flowListSp)
    }

    @Throws(com.xiaomi.thain.common.exception.ThainException::class, java.text.ParseException::class, org.quartz.SchedulerException::class)
    override fun add(addFlowRq: AddFlowRq, addJobRqList: List<AddJobRq>, appId: String): Long {
        val localAddFlowRq = if (!addFlowRq.slaKill || addFlowRq.slaDuration == 0L) {
            addFlowRq.copy(slaKill = true, slaDuration = 3L * 60 * 60)
        } else {
            addFlowRq
        }.copy(createAppId = appId)
        val localAddFlowRqId = localAddFlowRq.id
        if (localAddFlowRqId != null && flowDao.flowExist(localAddFlowRqId)) {
            val updateFlowRq = UpdateFlowRq(localAddFlowRq, localAddFlowRqId)
            thainFacade.updateFlow(updateFlowRq, addJobRqList)
            return updateFlowRq.id
        }
        val flowId = thainFacade.addFlow(AddFlowAndJobsRq(localAddFlowRq, addJobRqList))
        flowDao.updateAppId(flowId, appId)
        return flowId
    }

    @Throws(org.quartz.SchedulerException::class)
    override fun delete(flowId: Long): Boolean {
        thainFacade.deleteFlow(flowId)
        return true
    }

    @Throws(com.xiaomi.thain.common.exception.ThainException::class, com.xiaomi.thain.common.exception.ThainRepeatExecutionException::class)
    override fun start(flowId: Long): Long {
        return thainFacade.startFlow(flowId)
    }

    override fun getFlow(flowId: Long): com.xiaomi.thain.common.model.FlowModel {
        return flowDao.getFlow(flowId).orElseThrow { com.xiaomi.thain.common.exception.ThainRuntimeException("Flow does not exist, flow Id:$flowId") }
    }

    override fun getJobModelList(flowId: Long): List<com.xiaomi.thain.common.model.JobModel> {
        return flowDao.getJobModelList(flowId)
    }

    override fun getComponentDefineStringMap(): Map<String, String> {
        return thainFacade.componentDefineJsonList
    }

    @Throws(com.xiaomi.thain.common.exception.ThainException::class, org.quartz.SchedulerException::class, java.io.IOException::class)
    override fun scheduling(flowId: Long) {
        thainFacade.schedulingFlow(flowId)
    }

    @Throws(com.xiaomi.thain.common.exception.ThainException::class, java.text.ParseException::class, org.quartz.SchedulerException::class, java.io.IOException::class)
    override fun updateCron(flowId: Long, cron: String?) {
        thainFacade.updateCron(flowId, cron)
    }

    @Throws(com.xiaomi.thain.common.exception.ThainException::class)
    override fun pause(flowId: Long) {
        thainFacade.pauseFlow(flowId)
    }

}