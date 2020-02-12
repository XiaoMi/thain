package com.xiaomi.thain.server.model.rp

import com.xiaomi.thain.common.model.JobExecutionModel
import com.xiaomi.thain.common.model.JobModel
import com.xiaomi.thain.common.model.dr.FlowExecutionDr

/**
 * Date 19-7-1 上午10:33
 * flow model 和 jobModel list
 *
 * @author liangyongrui@xiaomi.com
 */
data class FlowExecutionAllInfoRp(
        val flowExecutionModel: FlowExecutionDr,
        val jobModelList: List<JobModel>,
        val jobExecutionModelList: List<JobExecutionModel>
)
