package com.xiaomi.thain.server.mapper

import com.xiaomi.thain.common.model.JobExecutionModel
import com.xiaomi.thain.common.model.JobModel
import com.xiaomi.thain.common.model.dr.FlowExecutionDr
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

/**
 * @author liangyongrui@xiaomi.com
 */
@Component
interface FlowExecutionMapper {

    fun getAppIdAccessible(@Param("flowExecutionId") flowExecutionId: Long, @Param("appId") appId: String): Boolean

    fun getFlowExecutionList(@Param("flowId") flowId: Long, @Param("offset") offset: Int, @Param("limit") limit: Int): List<FlowExecutionDr>

    @Select("select count(*) from thain_flow_execution where flow_id = #{flowId}")
    fun getFlowExecutionCount(@Param("flowId") flowId: Long): Long

    fun getFlowExecution(flowExecutionId: Long): FlowExecutionDr?

    fun getJobModelList(flowExecutionId: Long): List<JobModel>

    fun getJobExecutionModelList(flowExecutionId: Long): List<JobExecutionModel>

    /**
     * get execution by flowId
     */
    fun getRunningExecutionIdsByFlowId(@Param("flowId") flowId: Long, @Param("status") status: Int): List<Long>
}
