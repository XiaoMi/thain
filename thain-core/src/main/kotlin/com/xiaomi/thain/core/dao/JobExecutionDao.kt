package com.xiaomi.thain.core.dao

import com.xiaomi.thain.common.constant.JobExecutionStatus
import com.xiaomi.thain.common.model.JobExecutionModel
import com.xiaomi.thain.core.mapper.JobExecutionMapper
import com.xiaomi.thain.core.process.service.MailService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.ibatis.session.SqlSessionFactory
import org.slf4j.LoggerFactory

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
class JobExecutionDao(private val sqlSessionFactory: SqlSessionFactory,
                      private val mailService: MailService) {

    private val log = LoggerFactory.getLogger(this.javaClass)!!
    private val dataReserveDays = sqlSessionFactory.configuration.variables["dataReserveDays"] as Int

    private fun <T> execute(function: (JobExecutionMapper) -> T?): T? {
        try {
            sqlSessionFactory.openSession().use { sqlSession ->
                val apply = function(sqlSession.getMapper(JobExecutionMapper::class.java))
                sqlSession.commit()
                return apply
            }
        } catch (e: Exception) {
            log.error("", e)
            mailService.sendSeriousError(ExceptionUtils.getStackTrace(e))
            return null
        }
    }

    /**
     * 数据库插入JobExecutionModel
     * jobExecutionModel 会被插入自增id
     */
    fun add(jobExecutionModel: JobExecutionModel) {
        execute { it.add(jobExecutionModel) }
    }

    fun updateLogs(jobExecutionId: Long, logs: String) {
        execute { it.updateLogs(jobExecutionId, logs) }
    }

    fun updateStatus(jobExecutionId: Long, status: JobExecutionStatus) {
        execute { it.updateStatus(jobExecutionId, status.code) }
    }

    fun updateCreateTimeAndStatus(jobExecutionId: Long, status: JobExecutionStatus) {
        execute {
            it.updateCreateTime(jobExecutionId)
            it.updateStatus(jobExecutionId, status.code)
        }
    }

    fun killJobExecution(flowExecutionId: Long) {
        execute { it.killJobExecution(flowExecutionId) }
    }

    fun deleteJobExecutionByFlowExecutionIds(flowExecutionIds: List<Long>) {
        if (flowExecutionIds.isEmpty()) {
            return
        }
        execute { it.deleteJobExecutionByFlowExecutionIds(flowExecutionIds) }
    }

    fun cleanUpExpiredFlowExecution() {
        execute { it.cleanUpExpiredFlowExecution(dataReserveDays) }
    }
}
