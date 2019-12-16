package com.xiaomi.thain.server.service.impl

import com.xiaomi.thain.common.utils.isNotNull
import com.xiaomi.thain.server.dao.FlowDao
import com.xiaomi.thain.server.dao.FlowExecutionDao
import com.xiaomi.thain.server.service.PermissionService
import org.springframework.stereotype.Service

/**
 * @author liangyongrui@xiaomi.com
 * @date 18-12-6 上午11:47
 */
@Service
class PermissionServiceImpl(private val flowExecutionDao: FlowExecutionDao, private val flowDao: FlowDao) : PermissionService {
    override fun getFlowAccessible(flowId: Long, userId: String, appIds: Set<String>?): Boolean {
        return flowDao.getAccessible(flowId, userId, appIds)
    }

    override fun getFlowAccessible(flowId: Long, appId: String): Boolean {
        return flowDao.getAccessible(flowId, appId)
    }

    override fun getFlowExecutionAccessible(flowExecutionId: Long, userId: String, appIds: Set<String>?): Boolean {
        return flowExecutionDao.getFlowExecution(flowExecutionId)
                ?.flowId
                ?.let { flowDao.getFlow(it) }
                ?.takeIf { it.createUser == userId || (appIds != null && appIds.contains(it.createAppId)) }
                .isNotNull()
    }

    override fun getFlowExecutionAccessible(flowExecutionId: Long, appId: String): Boolean {
        return flowExecutionDao.getAccessible(flowExecutionId, appId)
    }

}
