package com.xiaomi.thain.core.process.runtime.storage

import java.util.concurrent.ConcurrentHashMap

/**
 * Date 19-5-17 上午9:43
 *
 * 保存流程产生的中间结果
 *
 * @author liangyongrui@xiaomi.com
 */
const val GLOBAL_JOB_NAME = "g"

class FlowExecutionStorage {

    val storageMap = ConcurrentHashMap<Pair<String, String>, Any>()

    private val finishJob = ConcurrentHashMap.newKeySet<String>()

    fun put(jobName: String, key: String, value: Any) {
        storageMap[jobName to key] = value
    }

    /**
     * 添加到完成列表
     */
    fun addFinishJob(jobName: String) {
        finishJob.add(jobName)
    }

    /**
     * 判断Job name 是否finish
     */
    fun finished(jobName: String): Boolean {
        return finishJob.contains(jobName)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(jobName: String, key: String): T? {
        return storageMap[jobName to key]?.let { it as T }
    }

    companion object {
        private val FLOW_EXECUTION_STORAGE_MAP: MutableMap<Long, FlowExecutionStorage> = ConcurrentHashMap()

        @JvmStatic
        fun getInstance(flowExecutionId: Long): FlowExecutionStorage {
            return FLOW_EXECUTION_STORAGE_MAP.computeIfAbsent(flowExecutionId) { FlowExecutionStorage() }
        }

        fun drop(flowExecutionId: Long) {
            FLOW_EXECUTION_STORAGE_MAP.remove(flowExecutionId)
        }
    }

}
