package com.xiaomi.thain.core.process

import com.xiaomi.thain.common.model.dr.FlowExecutionDr
import com.xiaomi.thain.core.dao.*
import com.xiaomi.thain.core.process.runtime.notice.MailNotice
import com.xiaomi.thain.core.process.service.ComponentService
import com.xiaomi.thain.core.process.service.MailService
import com.xiaomi.thain.core.thread.pool.ThainThreadPool
import java.util.concurrent.LinkedBlockingQueue

class ProcessEngineStorage(val flowExecutionThreadPool: ThainThreadPool,
                           val processEngineId: String,
                           @JvmField
                           val flowDao: FlowDao,
                           @JvmField
                           val flowExecutionDao: FlowExecutionDao,
                           val jobDao: JobDao,
                           @JvmField
                           val jobExecutionDao: JobExecutionDao,
                           val x5ConfigDao: X5ConfigDao,
                           @JvmField
                           val mailService: MailService,
                           @JvmField
                           val componentService: ComponentService,
                           private val flowExecutionJobExecutionThreadPool: (Long) -> ThainThreadPool,
                           @JvmField
                           val flowExecutionWaitingQueue: LinkedBlockingQueue<FlowExecutionDr>) {

    fun getMailNotice(noticeEmail: String): MailNotice {
        return MailNotice.getInstance(mailService, noticeEmail)
    }

    fun flowExecutionJobThreadPool(flowExecutionId: Long): ThainThreadPool {
        return flowExecutionJobExecutionThreadPool(flowExecutionId)
    }

}
