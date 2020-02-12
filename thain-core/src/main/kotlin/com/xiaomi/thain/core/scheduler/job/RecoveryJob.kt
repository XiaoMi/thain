/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.scheduler.job

import com.alibaba.fastjson.JSON
import com.xiaomi.thain.common.constant.FlowExecutionStatus
import com.xiaomi.thain.common.utils.HostUtils
import com.xiaomi.thain.core.process.ProcessEngine
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * 恢复失败任务
 *
 * @author liangyongrui
 */
class RecoveryJob private constructor(private val processEngine: ProcessEngine) : Job {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    override fun execute(context: JobExecutionContext) {
        val flowExecutionDao = processEngine.processEngineStorage.flowExecutionDao
        val jobExecutionDao = processEngine.processEngineStorage.jobExecutionDao
        val flowExecutionDrList = flowExecutionDao.dead
        if (flowExecutionDrList.isEmpty()) {
            return
        }
        val ids = flowExecutionDrList.map { it.id }
        flowExecutionDao.reWaiting(ids)
        flowExecutionDrList
                .filter { it.status == FlowExecutionStatus.RUNNING.code }
                .map { it.flowId }
                .distinct()
                .forEach { flowId -> processEngine.processEngineStorage.flowDao.killFlow(flowId) }
        jobExecutionDao.deleteJobExecutionByFlowExecutionIds(ids)
        log.info("Scanned some dead flows: \n" + JSON.toJSONString(flowExecutionDrList))
        processEngine.processEngineStorage.flowExecutionWaitingQueue.addAll(flowExecutionDrList)
        val hostInfo = HostUtils.getHostInfo()
        flowExecutionDrList.forEach { (id) -> flowExecutionDao.updateHostInfo(id, hostInfo) }
    }

    companion object {
        private val RECOVERY_JOB_MAP: MutableMap<String, RecoveryJob> = ConcurrentHashMap()

        @JvmStatic
        fun getInstance(processEngine: ProcessEngine): RecoveryJob {
            return RECOVERY_JOB_MAP.computeIfAbsent(processEngine.processEngineId) { RecoveryJob(processEngine) }
        }
    }

}
