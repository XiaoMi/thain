package com.xiaomi.thain.server.service

import com.xiaomi.thain.common.model.rq.AddFlowRq
import com.xiaomi.thain.common.model.rq.AddJobRq

/**
 * @author liangyongrui
 */
@org.springframework.stereotype.Service
interface FlowService {
    fun getFlowList(flowListSp: com.xiaomi.thain.server.model.sp.FlowListSp): List<com.xiaomi.thain.common.model.FlowModel?>
    fun getFlowListCount(flowListSp: com.xiaomi.thain.server.model.sp.FlowListSp): Long
    /**
     * 创建或更新任务
     */
    @Throws(com.xiaomi.thain.common.exception.ThainException::class, java.text.ParseException::class, org.quartz.SchedulerException::class)
    fun add(addFlowRq: AddFlowRq, addJobRqList: List<AddJobRq>, appId: String): Long

    /**
     * 删除
     */
    @Throws(org.quartz.SchedulerException::class)
    fun delete(flowId: Long): Boolean

    /**
     * 立即执行一次, 返回flow execution id
     */
    @Throws(com.xiaomi.thain.common.exception.ThainException::class, com.xiaomi.thain.common.exception.ThainRepeatExecutionException::class)
    fun start(flowId: Long): Long

    fun getFlow(flowId: Long): com.xiaomi.thain.common.model.FlowModel
    fun getJobModelList(flowId: Long): List<com.xiaomi.thain.common.model.JobModel>
    fun getComponentDefineStringMap(): Map<String, String>
    @Throws(com.xiaomi.thain.common.exception.ThainException::class, org.quartz.SchedulerException::class, java.io.IOException::class)
    fun scheduling(flowId: Long)

    @Throws(com.xiaomi.thain.common.exception.ThainException::class)
    fun pause(flowId: Long)

    @Throws(com.xiaomi.thain.common.exception.ThainException::class, java.text.ParseException::class, org.quartz.SchedulerException::class, java.io.IOException::class)
    fun updateCron(flowId: Long, cron: String?)
}
