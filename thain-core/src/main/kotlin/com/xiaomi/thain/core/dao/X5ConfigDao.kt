package com.xiaomi.thain.core.dao

import com.xiaomi.thain.core.mapper.X5ConfigMapper
import com.xiaomi.thain.core.model.dr.X5ConfigDr
import com.xiaomi.thain.core.process.service.MailService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.ibatis.session.SqlSessionFactory
import org.slf4j.LoggerFactory

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
class X5ConfigDao(
        private val sqlSessionFactory: SqlSessionFactory,
        private val mailService: MailService
) {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    /**
     * 自动释放sqlSession，事务执行mapper
     *
     * @param function function 是一个事务
     * @return 自定义的返回值
     */
    private fun <T> execute(function: (X5ConfigMapper) -> T?): T? {
        return try {
            sqlSessionFactory.openSession().use { sqlSession ->
                function(sqlSession.getMapper(X5ConfigMapper::class.java))
                        .apply { sqlSession.commit() }
            }
        } catch (e: Exception) {
            log.error("", e)
            mailService.sendSeriousError(ExceptionUtils.getStackTrace(e))
            null
        }
    }

    fun getX5ConfigByAppId(appId: String): X5ConfigDr? {
        return execute { it.getX5ConfigByAppId(appId) }
    }

}
