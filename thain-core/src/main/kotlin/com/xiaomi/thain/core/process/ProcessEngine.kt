package com.xiaomi.thain.core.process

import com.xiaomi.thain.common.constant.FlowSchedulingStatus
import com.xiaomi.thain.common.exception.ThainException
import com.xiaomi.thain.common.exception.ThainMissRequiredArgumentsException
import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.common.model.dr.FlowExecutionDr
import com.xiaomi.thain.core.ThainFacade
import com.xiaomi.thain.core.config.DatabaseHandler
import com.xiaomi.thain.core.dao.*
import com.xiaomi.thain.core.model.dr.FlowDr
import com.xiaomi.thain.core.model.rq.AddFlowRq
import com.xiaomi.thain.core.model.rq.AddJobRq
import com.xiaomi.thain.core.process.runtime.FlowExecutionLoader
import com.xiaomi.thain.core.process.runtime.heartbeat.FlowExecutionHeartbeat
import com.xiaomi.thain.core.process.service.ComponentService
import com.xiaomi.thain.core.process.service.MailService
import com.xiaomi.thain.core.thread.pool.ThainThreadPool
import org.apache.commons.lang3.StringUtils
import org.apache.ibatis.io.Resources
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.ibatis.session.SqlSessionFactory
import org.slf4j.LoggerFactory
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue

/**
 * Date 19-5-17 下午2:09
 *
 * @author liangyongrui@xiaomi.com
 */
class ProcessEngine(processEngineConfiguration: ProcessEngineConfiguration, val thainFacade: ThainFacade) {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @JvmField
    val processEngineId: String = UUID.randomUUID().toString()
    @JvmField
    val processEngineStorage: ProcessEngineStorage
    val flowExecutionLoader: FlowExecutionLoader
    val sqlSessionFactory: SqlSessionFactory
    @Throws(IOException::class, SQLException::class)
    private fun createTable(connection: Connection) {
        val runner = ScriptRunner(connection)
        val driver = DriverManager.getDriver(connection.metaData.url).javaClass.name
        if ("org.h2.Driver" == driver) {
            runner.runScript(Resources.getResourceAsReader("sql/h2/quartz.sql"))
            runner.runScript(Resources.getResourceAsReader("sql/h2/thain.sql"))
        } else if ("com.mysql.cj.jdbc.Driver" == driver) {
            runner.runScript(Resources.getResourceAsReader("sql/mysql/quartz.sql"))
            runner.runScript(Resources.getResourceAsReader("sql/mysql/spring_session.sql"))
            runner.runScript(Resources.getResourceAsReader("sql/mysql/thain.sql"))
        }
    }

    @Throws(IOException::class, SQLException::class)
    private fun initData(connection: Connection) {
        val runner = ScriptRunner(connection)
        val driver = DriverManager.getDriver(connection.metaData.url).javaClass.name
        if ("org.h2.Driver" == driver) {
            runner.runScript(Resources.getResourceAsReader("sql/h2/init_data.sql"))
        } else if ("com.mysql.cj.jdbc.Driver" == driver) {
            runner.runScript(Resources.getResourceAsReader("sql/mysql/init_data.sql"))
        }
    }

    /**
     * 插入flow
     * 成功返回 flow id
     */
    fun addFlow(addFlowRq: AddFlowRq, jobModelList: List<AddJobRq>): Optional<Long> {
        try {
            var schedulingStatus = FlowSchedulingStatus.NOT_SET
            if (StringUtils.isNotBlank(addFlowRq.cron)) {
                schedulingStatus = FlowSchedulingStatus.SCHEDULING
            }
            return processEngineStorage.flowDao.addFlow(addFlowRq, jobModelList, schedulingStatus)
        } catch (e: Exception) {
            log.error("addFlow:", e)
        }
        return Optional.empty()
    }

    /**
     * 删除flow
     * 返回删除job个数
     */
    fun deleteFlow(flowId: Long) {
        processEngineStorage.flowDao.deleteFlow(flowId)
    }

    /**
     * 手动触发一次
     */
    fun startProcess(flowId: Long, variables: Map<String, String>): Long {
        return flowExecutionLoader.startAsync(flowId, variables)
    }

    fun retryFlow(flowId: Long, retryNumber: Int, variables: Map<String, String>): Long {
        return flowExecutionLoader.retryAsync(flowId, retryNumber, variables)
    }

    @Throws(ThainException::class)
    fun getFlow(flowId: Long): FlowDr {
        return processEngineStorage.flowDao
                .getFlow(flowId).orElseThrow { ThainException("failed to obtain flow") }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProcessEngine

        if (thainFacade != other.thainFacade) return false
        if (log != other.log) return false
        if (processEngineId != other.processEngineId) return false
        if (processEngineStorage != other.processEngineStorage) return false
        if (flowExecutionLoader != other.flowExecutionLoader) return false
        if (sqlSessionFactory != other.sqlSessionFactory) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thainFacade.hashCode()
        result = 31 * result + log.hashCode()
        result = 31 * result + processEngineId.hashCode()
        result = 31 * result + processEngineStorage.hashCode()
        result = 31 * result + flowExecutionLoader.hashCode()
        result = 31 * result + sqlSessionFactory.hashCode()
        return result
    }

    companion object {
        private val PROCESS_ENGINE_MAP: MutableMap<String, ProcessEngine> = ConcurrentHashMap()
        /**
         * 用id获取流程实例
         */
        fun getInstance(processEngineId: String): ProcessEngine {
            return Optional.ofNullable(PROCESS_ENGINE_MAP[processEngineId]).orElseThrow { ThainRuntimeException("Failed to obtain process instance") }
        }

        @Throws(ThainMissRequiredArgumentsException::class, IOException::class, SQLException::class)
        fun newInstance(processEngineConfiguration: ProcessEngineConfiguration,
                        thainFacade: ThainFacade): ProcessEngine {
            return ProcessEngine(processEngineConfiguration, thainFacade)
        }
    }

    init {
        PROCESS_ENGINE_MAP[processEngineId] = this
        val flowExecutionJobExecutionThreadPool = { flowExecutionId: Long ->
            ThainThreadPool.getInstance(
                    "thain-job-execution-thread[flowExecutionId:$flowExecutionId]",
                    processEngineConfiguration.flowExecutionJobExecutionThreadPoolCoreSize)
        }
        val flowExecutionThreadPool = ThainThreadPool.getInstance("thain-flow-execution-thread",
                processEngineConfiguration.flowExecutionThreadPoolCoreSize)
        sqlSessionFactory = DatabaseHandler.newSqlSessionFactory(processEngineConfiguration.dataSource,
                processEngineConfiguration.dataReserveDays)
        when (processEngineConfiguration.initLevel) {
            "1" -> {
                createTable(processEngineConfiguration.dataSource.connection)
                initData(processEngineConfiguration.dataSource.connection)
            }
            "2" -> initData(processEngineConfiguration.dataSource.connection)
            else -> {
                // do nothing
            }
        }
        val userDao = UserDao.getInstance(sqlSessionFactory)
        val mailService = MailService.getInstance(processEngineConfiguration.mailHost,
                processEngineConfiguration.mailSender,
                processEngineConfiguration.mailSenderUsername,
                processEngineConfiguration.mailSenderPassword,
                userDao)
        val flowDao = FlowDao(sqlSessionFactory, mailService)
        val flowExecutionDao = FlowExecutionDao(sqlSessionFactory, mailService)
        val jobDao = JobDao(sqlSessionFactory, mailService)
        val jobExecutionDao = JobExecutionDao(sqlSessionFactory, mailService)
        val x5ConfigDao = X5ConfigDao(sqlSessionFactory, mailService)
        val componentService = ComponentService()
        val flowExecutionWaitingQueue = LinkedBlockingQueue<FlowExecutionDr>()
        processEngineStorage = ProcessEngineStorage.builder()
                .flowExecutionJobExecutionThreadPool(flowExecutionJobExecutionThreadPool)
                .flowExecutionThreadPool(flowExecutionThreadPool)
                .processEngineId(processEngineId)
                .flowDao(flowDao)
                .flowExecutionDao(flowExecutionDao)
                .jobDao(jobDao)
                .jobExecutionDao(jobExecutionDao)
                .x5ConfigDao(x5ConfigDao)
                .mailService(mailService)
                .componentService(componentService)
                .flowExecutionWaitingQueue(flowExecutionWaitingQueue)
                .build()
        flowExecutionLoader = FlowExecutionLoader(processEngineStorage)
        val flowExecutionHeartbeat = FlowExecutionHeartbeat(flowExecutionDao, mailService)
        flowExecutionHeartbeat.addCollections(flowExecutionWaitingQueue)
        flowExecutionHeartbeat.addCollections(flowExecutionLoader.runningFlowExecution)
    }
}
