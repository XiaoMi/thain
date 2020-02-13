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
                           val flowDao: FlowDao,
                           val flowExecutionDao: FlowExecutionDao,
                           val jobDao: JobDao,
                           val jobExecutionDao: JobExecutionDao,
                           val x5ConfigDao: X5ConfigDao,
                           val mailService: MailService,
                           val componentService: ComponentService,
                           private val flowExecutionJobExecutionThreadPool: (Long) -> ThainThreadPool,
                           val flowExecutionWaitingQueue: LinkedBlockingQueue<FlowExecutionDr>) {

    fun getMailNotice(noticeEmail: String): MailNotice {
        return MailNotice.getInstance(mailService, noticeEmail)
    }

    fun flowExecutionJobThreadPool(flowExecutionId: Long): ThainThreadPool {
        return flowExecutionJobExecutionThreadPool(flowExecutionId)
    }

}
