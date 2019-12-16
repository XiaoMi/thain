package com.xiaomi.thain.core.dao

import com.xiaomi.thain.common.constant.FlowLastRunStatus
import com.xiaomi.thain.common.constant.FlowSchedulingStatus
import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.core.mapper.FlowMapper
import com.xiaomi.thain.core.model.dp.AddFlowDp
import com.xiaomi.thain.core.model.dp.AddJobDp
import com.xiaomi.thain.core.model.dp.UpdateFlowDp
import com.xiaomi.thain.core.model.dr.FlowDr
import com.xiaomi.thain.core.model.rq.AddFlowRq
import com.xiaomi.thain.core.model.rq.AddJobRq
import com.xiaomi.thain.core.process.service.MailService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.ibatis.session.SqlSessionFactory
import org.slf4j.LoggerFactory
import java.util.*
import java.util.stream.Collectors

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
class FlowDao(
        private val sqlSessionFactory: SqlSessionFactory,
        private val mailService: MailService
) {
    private val log = LoggerFactory.getLogger(this.javaClass)!!
    private val dataReserveDays = sqlSessionFactory.configuration.variables["dataReserveDays"] as Int

    /**
     * 自动释放sqlSession，事务执行mapper
     *
     * @param function function 是一个事务
     * @return 自定义的返回值
     */
    private fun <T> execute(function: (FlowMapper) -> T?): Optional<T> {
        try {
            sqlSessionFactory.openSession().use { sqlSession ->
                val apply = function(sqlSession.getMapper(FlowMapper::class.java))
                sqlSession.commit()
                return Optional.ofNullable(apply)
            }
        } catch (e: Exception) {
            log.error("", e)
            mailService.sendSeriousError(ExceptionUtils.getStackTrace(e))
            return Optional.empty()
        }
    }

    /**
     * 数据库插入flow，成功flowModel插入id
     */
    fun addFlow(addFlowRq: AddFlowRq,
                jobModelList: List<AddJobRq>,
                flowSchedulingStatus: FlowSchedulingStatus): Optional<Long> {
        return execute {
            val addFlowDp = AddFlowDp(addFlowRq, flowSchedulingStatus.code)
            it.addFlow(addFlowDp)
            if (addFlowDp.id == null) {
                throw ThainRuntimeException("add flow error")
            }
            it.addJobList(jobModelList.stream()
                    .map { job: AddJobRq -> AddJobDp.getInstance(job, addFlowDp.id) }
                    .collect(Collectors.toList()))
            addFlowDp.id
        }
    }

    /**
     * 更新flow
     */
    fun updateFlow(updateFlowDp: UpdateFlowDp, jobModelList: List<AddJobRq>) {
        execute {
            it.updateFlow(updateFlowDp)
            it.invalidJobList(updateFlowDp.id)
            if (jobModelList.isEmpty()) {
                return@execute
            }
            it.addJobList(jobModelList.stream()
                    .map { job: AddJobRq -> AddJobDp.getInstance(job, updateFlowDp.id) }
                    .collect(Collectors.toList()))
        }
    }

    /**
     * 删除flow
     */
    fun deleteFlow(flowId: Long) {
        execute {
            it.deleteFlow(flowId)
            it.deleteJob(flowId)
        }
    }

    /**
     * 根据flow id 获取FlowModel
     */
    fun getFlow(flowId: Long): Optional<FlowDr> {
        return execute { it.getFlow(flowId) }
    }

    /**
     * 修改最后一次运行状态
     *
     * @param flowId flow id
     * @param status 状态码
     */
    fun updateLastRunStatus(flowId: Long, status: FlowLastRunStatus) {
        execute { it.updateLastRunStatus(flowId, status.code) }
    }

    fun pauseFlow(flowId: Long) {
        execute { it.updateSchedulingStatus(flowId, FlowSchedulingStatus.PAUSE.code) }
    }

    fun killFlow(flowId: Long) {
        execute { it.updateLastRunStatus(flowId, FlowLastRunStatus.KILLED.code) }
    }

    fun updateSchedulingStatus(flowId: Long, scheduling: FlowSchedulingStatus) {
        execute { it.updateSchedulingStatus(flowId, scheduling.code) }
    }

    fun cleanUpExpiredAndDeletedFlow() {
        execute { it.cleanUpExpiredAndDeletedFlow(dataReserveDays) }
    }

}
