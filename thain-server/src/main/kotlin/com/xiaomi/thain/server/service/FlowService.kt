package com.xiaomi.thain.server.service

import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException
import com.xiaomi.thain.common.model.dr.FlowDr
import com.xiaomi.thain.common.model.dr.JobDr
import com.xiaomi.thain.common.model.rq.AddFlowRq
import com.xiaomi.thain.common.model.rq.AddJobRq
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
    fun getFlowList(flowListSp: FlowListSp): List<FlowDr>
    fun getFlowListCount(flowListSp: FlowListSp): Long
    /**
     * 创建或更新任务
     */
    fun add(addFlowRq: AddFlowRq, addJobRqList: List<AddJobRq>, appId: String): Long

    /**
     * 删除
     */
    @Throws(org.quartz.SchedulerException::class)
    fun delete(flowId: Long): Boolean

    /**
     * 立即执行一次, 返回flow execution id
     */
    @Throws(ThainException::class, ThainRepeatExecutionException::class)
    fun start(flowId: Long): Long

    fun getFlow(flowId: Long): FlowDr?
    fun getJobModelList(flowId: Long): List<JobDr>
    fun getComponentDefineStringMap(): Map<String, String>
    @Throws(ThainException::class, SchedulerException::class, IOException::class)
    fun scheduling(flowId: Long)

    @Throws(ThainException::class)
    fun pause(flowId: Long)

    @Throws(ThainException::class, ParseException::class, SchedulerException::class, IOException::class)
    fun updateCron(flowId: Long, cron: String?)
}
