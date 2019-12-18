package com.xiaomi.thain.core.dao

import com.xiaomi.thain.common.model.FlowExecutionModel
import com.xiaomi.thain.common.model.dp.AddFlowExecutionDp
import com.xiaomi.thain.common.model.dr.FlowExecutionDr
import com.xiaomi.thain.core.mapper.FlowExecutionMapper
import com.xiaomi.thain.core.process.service.MailService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.ibatis.session.SqlSessionFactory
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
class FlowExecutionDao(
        private val sqlSessionFactory: SqlSessionFactory,
        private val mailService: MailService) {

    private val log = LoggerFactory.getLogger(this.javaClass)!!
    private val dataReserveDays = sqlSessionFactory.configuration.variables["dataReserveDays"] as Int

    /**
     * 自动释放sqlSession，事务执行mapper
     *
     * @param function function 是一个事务
     * @return 自定义的返回值
     */
    private fun <T> execute(function: (FlowExecutionMapper) -> T?): Optional<T> {
        try {
            sqlSessionFactory.openSession().use { sqlSession ->
                val apply = function(sqlSession.getMapper(FlowExecutionMapper::class.java))
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
     * 数据库插入flowExecution
     */
    fun addFlowExecution(addFlowExecutionDp: AddFlowExecutionDp) {
        execute { it.addFlowExecution(addFlowExecutionDp) }
    }

    /**
     * 更新flowExecution日志
     */
    fun updateLogs(flowExecutionId: Long, content: String) {
        execute { it.updateLogs(flowExecutionId, content) }
    }

    fun updateFlowExecutionStatus(flowExecutionId: Long, status: Int) {
        execute { it.updateFlowExecutionStatus(flowExecutionId, status) }
    }

    /**
     * clean up expired flow execution
     */
    fun cleanUpExpiredFlowExecution() {
        execute { it.cleanUpExpiredFlowExecution(dataReserveDays) }
    }

    fun getFlowExecution(flowExecutionId: Long): Optional<FlowExecutionDr> {
        return execute { it.getFlowExecution(flowExecutionId) }
    }

    fun getLatest(flowId: Long, numbers: Long): List<FlowExecutionModel> {
        return execute { it.getLatest(flowId, numbers) }.orElse(emptyList())
    }

    fun setFlowExecutionHeartbeat(flowExecutionIds: List<Long>) {
        if (flowExecutionIds.isEmpty()) {
            return
        }
        execute { it.setFlowExecutionHeartbeat(flowExecutionIds) }
    }

    /**
     * 获取超过2min没心跳的任务
     */
    val dead: List<FlowExecutionDr>
        get() = execute { it.dead }.orElseGet { emptyList() }

    /**
     * 重新排队
     */
    fun reWaiting(flowExecutionIds: List<Long>) {
        execute { it.reWaiting(flowExecutionIds) }
    }

    fun updateHostInfo(id: Long, hostInfo: String) {
        execute { it.updateHostInfo(id, hostInfo) }
    }
}
