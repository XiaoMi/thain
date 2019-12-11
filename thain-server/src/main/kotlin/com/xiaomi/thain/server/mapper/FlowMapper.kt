package com.xiaomi.thain.server.mapper

import com.xiaomi.thain.common.model.FlowModel
import com.xiaomi.thain.common.model.JobModel
import com.xiaomi.thain.common.model.dr.FlowDr
import com.xiaomi.thain.server.model.sp.FlowListSp
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Component

/**
 * @author liangyongrui@xiaomi.com
 */
@Mapper
@Component
interface FlowMapper {
    fun getUserAccessible(@Param("flowId") flowId: Long, @Param("userId") userId: String,
                          @Param("appIds") appIds: Set<String>?): Boolean

    fun getAppIdAccessible(@Param("flowId") flowId: Long, @Param("appId") appId: String): Boolean

    @Deprecated("should use dr")
    fun getFlowList(flowListSp: FlowListSp): List<FlowModel>

    fun getFlowListCount(flowListSp: FlowListSp): Long
    fun flowExist(flowId: Long): Boolean
    fun updateAppId(@Param("flowId") flowId: Long, @Param("appId") appId: String)
    fun getFlowIdByFlowExecutionId(flowExecutionId: Long): Long
    fun getFlow(flowId: Long): FlowDr?

    @Deprecated("should use dr")
    fun getJobModelList(flowId: Long): List<JobModel>
}
