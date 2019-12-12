package com.xiaomi.thain.server.dao

import com.xiaomi.thain.common.model.dr.FlowDr
import com.xiaomi.thain.common.model.dr.JobDr
import com.xiaomi.thain.server.mapper.FlowMapper
import com.xiaomi.thain.server.model.sp.FlowListSp
import org.springframework.stereotype.Repository

/**
 * @author liangyongrui@xiaomi.com
 */
@Repository
class FlowDao(private val flowMapper: FlowMapper) {
    fun getAccessible(flowId: Long, userId: String, appIds: Set<String>?): Boolean {
        return flowMapper.getUserAccessible(flowId, userId, appIds)
    }

    fun getFlowList(flowListSp: FlowListSp): List<FlowDr> {
        return flowMapper.getFlowList(flowListSp)
    }

    fun getFlowListCount(flowListSp: FlowListSp): Long {
        return flowMapper.getFlowListCount(flowListSp)
    }

    fun flowExist(flowId: Long): Boolean {
        return flowMapper.flowExist(flowId)
    }

    fun updateAppId(flowId: Long, appId: String) {
        flowMapper.updateAppId(flowId, appId)
    }

    fun getFlow(flowId: Long): FlowDr? {
        return flowMapper.getFlow(flowId)
    }

    fun getJobModelList(flowId: Long): List<JobDr> {
        return flowMapper.getJobModelList(flowId)
    }

    fun getAccessible(flowId: Long, appId: String): Boolean {
        return flowMapper.getAppIdAccessible(flowId, appId)
    }

}
