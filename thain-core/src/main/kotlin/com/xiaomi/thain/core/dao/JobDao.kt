package com.xiaomi.thain.core.dao

import com.xiaomi.thain.core.mapper.JobMapper
import com.xiaomi.thain.core.model.dr.JobDr
import com.xiaomi.thain.core.process.service.MailService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.ibatis.session.SqlSessionFactory
import org.slf4j.LoggerFactory

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
class JobDao(
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
    private fun <T> execute(function: (JobMapper) -> T?): T? {
        try {
            sqlSessionFactory.openSession().use { sqlSession ->
                val apply = function(sqlSession.getMapper(JobMapper::class.java))
                sqlSession.commit()
                return apply
            }
        } catch (e: Exception) {
            log.error("", e)
            mailService.sendSeriousError(ExceptionUtils.getStackTrace(e))
            return null
        }
    }

    fun getJobs(flowId: Long): List<JobDr> {
        return execute { it.getJobs(flowId) } ?: listOf()
    }

    fun cleanUpExpiredAndDeletedJob() {
        execute { it.cleanUpExpiredAndDeletedJob(dataReserveDays) }
    }

}
