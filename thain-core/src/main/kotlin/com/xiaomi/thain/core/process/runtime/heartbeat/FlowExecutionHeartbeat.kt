package com.xiaomi.thain.core.process.runtime.heartbeat

import com.xiaomi.thain.common.model.dr.FlowExecutionDr
import com.xiaomi.thain.core.dao.FlowExecutionDao
import com.xiaomi.thain.core.process.service.MailService
import com.xiaomi.thain.core.thread.pool.ThainThreadPool
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 定时发送心跳
 *
 * @author liangyongrui
 */
class FlowExecutionHeartbeat(private val flowExecutionDao: FlowExecutionDao,
                             private val mailService: MailService) {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    private val collections = Collections.newSetFromMap(IdentityHashMap<Collection<FlowExecutionDr>, Boolean>())
    fun addCollections(collection: Collection<FlowExecutionDr>) {
        collections.add(collection)
    }

    /**
     * 每30s发送一次心跳
     */
    private fun sendHeartbeat() {
        while (true) {
            try {
                collections.flatten().map { it.id }.toList()
                        .let { flowExecutionDao.setFlowExecutionHeartbeat(it) }
                TimeUnit.SECONDS.sleep(30)
            } catch (e: Throwable) {
                mailService.sendSeriousError(ExceptionUtils.getStackTrace(e))
                log.error("", e)
            }
        }
    }

    init {
        log.info("init FlowExecutionHeartbeat")
        ThainThreadPool.DEFAULT_THREAD_POOL.execute { sendHeartbeat() }
    }
}
