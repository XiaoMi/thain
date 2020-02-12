package com.xiaomi.thain.core.process.component.tools.impl

import com.xiaomi.thain.common.exception.ThainRuntimeException
import com.xiaomi.thain.common.utils.HttpUtils
import com.xiaomi.thain.common.utils.X5Utils
import com.xiaomi.thain.common.utils.ifNull
import com.xiaomi.thain.component.tools.ComponentTools
import com.xiaomi.thain.core.constant.LogLevel
import com.xiaomi.thain.core.model.dr.JobDr
import com.xiaomi.thain.core.process.ProcessEngineStorage
import com.xiaomi.thain.core.process.runtime.log.JobExecutionLogHandler
import com.xiaomi.thain.core.process.runtime.storage.FlowExecutionStorage
import java.io.IOException

/**
 * Date 19-5-30 下午4:31
 *
 * @author liangyongrui@xiaomi.com
 */
class ComponentToolsImpl(private val jobDr: JobDr,
                         private val jobExecutionId: Long,
                         private val flowExecutionId: Long,
                         private val processEngineStorage: ProcessEngineStorage) : ComponentTools {

    private val flowExecutionStorage = FlowExecutionStorage.getInstance(flowExecutionId)
    private val log = JobExecutionLogHandler.getInstance(jobExecutionId, processEngineStorage)
    private val mailService = processEngineStorage.mailService

    override fun sendMail(to: List<String>, subject: String, content: String) {
        mailService.send(to, subject, content)
    }

    /**
     * 保存当前节点产生的数据
     *
     * @param key   数据的key
     * @param value 数据的value
     */
    override fun putStorage(key: String, value: Any) {
        flowExecutionStorage.put(jobDr.name, key, value)
    }

    /**
     * 获取流程已经产生的数据
     *
     * @param jobName 节点名称
     * @param key     key
     * @param <T>     自动强制转换
    </T> */
    override fun <T> getStorageValue(jobName: String, key: String): T? {
        return flowExecutionStorage.get(jobName, key)
    }

    /**
     * 获取流程已经产生的数据
     *
     * @param jobName      节点名称
     * @param key          key
     * @param defaultValue 默认值
     * @param <T>          自动强制转换
     * @return 返回对应值, 值不存在则返回defaultValue
    </T> */
    override fun <T> getStorageValueOrDefault(jobName: String, key: String, defaultValue: T): T {
        return getStorageValue<T>(jobName, key) ?: defaultValue
    }

    /**
     * 增加debug日志
     */
    override fun addDebugLog(content: String) {
        log.add(content, LogLevel.DEBUG)
    }

    /**
     * 增加info日志
     */
    override fun addInfoLog(content: String) {
        log.add(content, LogLevel.INFO)
    }

    /**
     * 增加warning日志
     */
    override fun addWarnLog(content: String) {
        log.add(content, LogLevel.WARN)
    }

    /**
     * 增加error日志
     */
    override fun addErrorLog(content: String) {
        log.add(content, LogLevel.ERROR)
    }

    @Throws(IOException::class)
    override fun httpGet(url: String, data: Map<String, String>): String {
        return HttpUtils.get(url, data)
    }

    @Throws(IOException::class)
    override fun httpPost(url: String, headers: Map<String, String>, data: Map<String, *>): String {
        return HttpUtils.post(url, headers, data)
    }

    override fun getJobExecutionId(): Long {
        return jobExecutionId
    }

    override fun getStorage(): Map<Pair<String, String>, Any> {
        return flowExecutionStorage.storageMap
    }

    override fun httpX5Post(url: String, data: Map<String, String>): String {
        return (processEngineStorage.flowExecutionDao.getFlowExecution(flowExecutionId)
                ?: throw ThainRuntimeException())
                .flowId
                .let { processEngineStorage.flowDao.getFlow(it) ?: throw ThainRuntimeException() }.createAppId
                .also {
                    if (it == "thain") {
                        throw ThainRuntimeException("Page creation flow cannot send x5 request")
                    }
                }.let { processEngineStorage.x5ConfigDao.getX5ConfigByAppId(it) }
                .ifNull { throw ThainRuntimeException("X5 app id for flow does not exist") }
                .let { HttpUtils.postForm(url, X5Utils.buildX5Request(it.appId, it.appKey, data)) }
    }

}
