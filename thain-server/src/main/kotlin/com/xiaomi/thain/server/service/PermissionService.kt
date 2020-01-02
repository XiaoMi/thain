package com.xiaomi.thain.server.service

import com.xiaomi.thain.common.utils.isNotNull
import com.xiaomi.thain.server.dao.FlowDao
import com.xiaomi.thain.server.dao.FlowExecutionDao
import org.springframework.stereotype.Service

/**
 * @author liangyongrui@xiaomi.com
 * @date 18-12-6 上午11:47
 */
@Service
class PermissionService(private val flowExecutionDao: FlowExecutionDao, private val flowDao: FlowDao) {
    /**
     * 判断username 是否有权限访问、操作flowId指定的flow
     *
     * @param appIds appId 列表
     * @param userId 用户名
     * @param flowId flow id
     * @return 有权限返回true
     */
     fun getFlowAccessible(flowId: Long, userId: String, appIds: Set<String>?): Boolean {
        return flowDao.getAccessible(flowId, userId, appIds)
    }

    /**
     * 判断appId 是否有权限访问、操作flowId
     *
     * @param appId  appId
     * @param flowId flow id
     * @return 有权限返回true
     */
     fun getFlowAccessible(flowId: Long, appId: String): Boolean {
        return flowDao.getAccessible(flowId, appId)
    }

    /**
     * 判断username 是否有权限访问、操作flowExecutionId指定的flowExecution
     *
     * @param appIds          appId 列表
     * @param userId          用户名
     * @param flowExecutionId flowExecutionId
     * @return 有权限返回true
     */
     fun getFlowExecutionAccessible(flowExecutionId: Long, userId: String, appIds: Set<String>?): Boolean {
        return flowExecutionDao.getFlowExecution(flowExecutionId)
                ?.flowId
                ?.let { flowDao.getFlow(it) }
                ?.takeIf { it.createUser == userId || (appIds != null && appIds.contains(it.createAppId)) }
                .isNotNull()
    }
    /**
     * 判断username 是否有权限访问、操作flowExecutionId指定的flowExecution
     *
     * @param appId           appId
     * @param flowExecutionId flowExecutionId
     * @return 有权限返回true
     */
     fun getFlowExecutionAccessible(flowExecutionId: Long, appId: String): Boolean {
        return flowExecutionDao.getAccessible(flowExecutionId, appId)
    }

}
