package com.xiaomi.thain.core.scheduler

import com.alibaba.fastjson.JSON
import com.xiaomi.thain.common.exception.scheduler.ThainSchedulerInitException
import com.xiaomi.thain.common.exception.scheduler.ThainSchedulerStartException
import com.xiaomi.thain.core.model.dr.FlowDr
import com.xiaomi.thain.core.process.ProcessEngine
import com.xiaomi.thain.core.scheduler.job.*
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.quartz.spi.TriggerFiredBundle
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*

/**
 * Date 19-5-17 下午1:41
 *
 * @author liangyongrui@xiaomi.com
 */

class SchedulerEngine(schedulerEngineConfiguration: SchedulerEngineConfiguration,
                      processEngine: ProcessEngine) {
    companion object {
        private const val SYSTEM_GROUP = "system"
    }

    private val log = LoggerFactory.getLogger(this.javaClass)!!
    private val scheduler: Scheduler

    private fun initRecovery() {
        val jobDetail = JobBuilder.newJob(RecoveryJob::class.java)
                .withIdentity("job_recovery", SYSTEM_GROUP)
                .build()
        val trigger: Trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger_recovery", SYSTEM_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?").withMisfireHandlingInstructionDoNothing())
                .build()
        scheduler.deleteJob(jobDetail.key)
        scheduler.scheduleJob(jobDetail, trigger)
    }

    private fun initCleanUp() {
        val jobDetail = JobBuilder.newJob(CleanJob::class.java)
                .withIdentity("job_clean_up", SYSTEM_GROUP)
                .build()
        val trigger: Trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger_clean_up", SYSTEM_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                .build()
        scheduler.deleteJob(jobDetail.key)
        scheduler.scheduleJob(jobDetail, trigger)
    }

    fun addSla(flowExecutionId: Long, flowDr: FlowDr) {
        val jobDetail = JobBuilder.newJob(SlaJob::class.java)
                .withIdentity("flowExecution_$flowExecutionId", "flowExecution")
                .usingJobData("flowExecutionId", flowExecutionId)
                .usingJobData("flowId", flowDr.id)
                .build()
        val trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger_$flowExecutionId", "flowExecution")
                .startAt(Date.from(Instant.now().plusSeconds(flowDr.slaDuration)))
                .build()
        scheduler.deleteJob(jobDetail.key)
        scheduler.scheduleJob(jobDetail, trigger)
    }

    @Throws(ThainSchedulerStartException::class)
    fun start() {
        try {
            scheduler.start()
        } catch (e: SchedulerException) {
            log.error("start", e)
            throw ThainSchedulerStartException(e.message!!)
        }
    }

    /**
     * 添加指定任务，加入调度
     *
     * @param flowId flow id
     * @param cron   cron
     */
    @Throws(SchedulerException::class)
    fun addFlow(flowId: Long, cron: String?) {
        val jobDetail = JobBuilder.newJob(FlowJob::class.java)
                .withIdentity("flow_$flowId", "flow")
                .usingJobData("flowId", flowId)
                .build()
        if (cron.isNullOrBlank()) {
            scheduler.deleteJob(jobDetail.key)
            return
        }
        val trigger: Trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger_$flowId", "flow")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build()
        scheduler.deleteJob(jobDetail.key)
        scheduler.scheduleJob(jobDetail, trigger)
    }

    /**
     * 删除调度
     */
    @Throws(SchedulerException::class)
    fun deleteFlow(flowId: Long) {
        scheduler.deleteJob(JobKey("flow_$flowId", "flow"))
    }

    fun addRetry(flowDr: FlowDr, retryNumber: Int, variables: Map<String, String>) {
        val jobDetail = JobBuilder.newJob(RetryFlowJob::class.java)
                .withIdentity("flow_retry_${flowDr.id}", "flow_retry")
                .usingJobData("flowId", flowDr.id)
                .usingJobData("retryNumber", retryNumber)
                .usingJobData("variables", JSON.toJSONString(variables))
                .build()
        val trigger = TriggerBuilder.newTrigger()
                .withIdentity("flow_retry_${flowDr.id}", "flow_retry")
                .startAt(Date.from(Instant.now().plusSeconds(flowDr.retryTimeInterval.toLong())))
                .build()
        scheduler.deleteJob(jobDetail.key)
        scheduler.scheduleJob(jobDetail, trigger)
    }

    init {
        try {
            val factory = StdSchedulerFactory()
            factory.initialize(schedulerEngineConfiguration.properties)
            scheduler = factory.scheduler.also {
                it.setJobFactory { bundle: TriggerFiredBundle, _ ->
                    Class.forName(bundle.jobDetail.jobClass.name)
                            .getMethod("getInstance", ProcessEngine::class.java)
                            .invoke(null, processEngine) as Job
                }
            }
            initCleanUp()
            initRecovery()
        } catch (e: Exception) {
            log.error("thain init failed", e)
            throw ThainSchedulerInitException(e.message!!)
        }
    }
}
