package com.xiaomi.thain.core.process

import com.xiaomi.thain.common.exception.ThainMissRequiredArgumentsException
import com.xiaomi.thain.common.model.dr.FlowExecutionDr
import com.xiaomi.thain.core.dao.*
import com.xiaomi.thain.core.process.runtime.notice.MailNotice
import com.xiaomi.thain.core.process.service.ComponentService
import com.xiaomi.thain.core.process.service.MailService
import com.xiaomi.thain.core.thread.pool.ThainThreadPool
import java.util.concurrent.LinkedBlockingQueue
import java.util.function.LongFunction

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

    class ProcessEngineStorageBuilder internal constructor() {
        private var flowExecutionThreadPool: ThainThreadPool? = null
        private var processEngineId: String? = null
        private var flowDao: FlowDao? = null
        private var flowExecutionDao: FlowExecutionDao? = null
        private var jobDao: JobDao? = null
        private var jobExecutionDao: JobExecutionDao? = null
        private var x5ConfigDao: X5ConfigDao? = null
        private var mailService: MailService? = null
        private var componentService: ComponentService? = null
        private var flowExecutionJobExecutionThreadPool: ((Long) -> ThainThreadPool)? = null
        private var flowExecutionWaitingQueue: LinkedBlockingQueue<FlowExecutionDr>? = null
        fun flowExecutionThreadPool(flowExecutionThreadPool: ThainThreadPool): ProcessEngineStorageBuilder {
            this.flowExecutionThreadPool = flowExecutionThreadPool
            return this
        }

        fun processEngineId(processEngineId: String): ProcessEngineStorageBuilder {
            this.processEngineId = processEngineId
            return this
        }

        fun flowDao(flowDao: FlowDao): ProcessEngineStorageBuilder {
            this.flowDao = flowDao
            return this
        }

        fun flowExecutionDao(flowExecutionDao: FlowExecutionDao): ProcessEngineStorageBuilder {
            this.flowExecutionDao = flowExecutionDao
            return this
        }

        fun jobDao(jobDao: JobDao): ProcessEngineStorageBuilder {
            this.jobDao = jobDao
            return this
        }

        fun jobExecutionDao(jobExecutionDao: JobExecutionDao): ProcessEngineStorageBuilder {
            this.jobExecutionDao = jobExecutionDao
            return this
        }

        fun x5ConfigDao(x5ConfigDao: X5ConfigDao): ProcessEngineStorageBuilder {
            this.x5ConfigDao = x5ConfigDao
            return this
        }

        fun mailService(mailService: MailService): ProcessEngineStorageBuilder {
            this.mailService = mailService
            return this
        }

        fun componentService(componentService: ComponentService): ProcessEngineStorageBuilder {
            this.componentService = componentService
            return this
        }

        fun flowExecutionJobExecutionThreadPool(flowExecutionJobExecutionThreadPool: (Long) -> ThainThreadPool): ProcessEngineStorageBuilder {
            this.flowExecutionJobExecutionThreadPool = flowExecutionJobExecutionThreadPool
            return this
        }

        fun flowExecutionWaitingQueue(flowExecutionWaitingQueue: LinkedBlockingQueue<FlowExecutionDr>): ProcessEngineStorageBuilder {
            this.flowExecutionWaitingQueue = flowExecutionWaitingQueue
            return this
        }

        fun build(): ProcessEngineStorage {
            return ProcessEngineStorage(flowExecutionThreadPool!!, processEngineId!!, flowDao!!, flowExecutionDao!!, jobDao!!, jobExecutionDao!!, x5ConfigDao!!, mailService!!, componentService!!, flowExecutionJobExecutionThreadPool!!, flowExecutionWaitingQueue!!)
        }

    }

    companion object {
        @Throws(ThainMissRequiredArgumentsException::class)
        fun builder(): ProcessEngineStorageBuilder {
            return ProcessEngineStorageBuilder()
        }
    }

}
