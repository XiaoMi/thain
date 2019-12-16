package com.xiaomi.thain.server.dao

import com.xiaomi.thain.common.model.FlowExecutionModel
import com.xiaomi.thain.common.model.JobExecutionModel
import com.xiaomi.thain.common.model.JobModel
import com.xiaomi.thain.common.model.dr.FlowExecutionDr
import com.xiaomi.thain.server.mapper.FlowExecutionMapper
import org.springframework.stereotype.Repository

/**
 * @author liangyongrui@xiaomi.com
 */
@Repository
class FlowExecutionDao(private val flowExecutionMapper: FlowExecutionMapper) {

    fun getFlowExecutionList(flowId: Long, page: Int, pageSize: Int): List<FlowExecutionModel> {
        val offset = (page - 1) * pageSize
        return flowExecutionMapper.getFlowExecutionList(flowId, offset, pageSize)
    }

    fun getFlowExecutionCount(flowId: Long): Long {
        return flowExecutionMapper.getFlowExecutionCount(flowId)
    }

    fun getFlowExecution(flowExecutionId: Long): FlowExecutionDr? {
        return flowExecutionMapper.getFlowExecution(flowExecutionId)
    }

    fun getJobModelList(flowExecutionId: Long): List<JobModel> {
        return flowExecutionMapper.getJobModelList(flowExecutionId)
    }

    fun getJobExecutionModelList(flowExecutionId: Long): List<JobExecutionModel> {
        return flowExecutionMapper.getJobExecutionModelList(flowExecutionId)
    }

    fun getAccessible(flowExecutionId: Long, appId: String): Boolean {
        return flowExecutionMapper.getAppIdAccessible(flowExecutionId, appId)
    }

    fun getRunningExecutionIdsByFlowId(flowId: Long): List<Long> {
        return flowExecutionMapper.getRunningExecutionIdsByFlowId(flowId, 1)
    }

}
