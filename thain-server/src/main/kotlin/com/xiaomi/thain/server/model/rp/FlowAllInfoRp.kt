package com.xiaomi.thain.server.model.rp

import com.xiaomi.thain.common.model.JobModel
import com.xiaomi.thain.common.model.dr.FlowDr

/**
 * Date 19-7-1 上午10:33
 * flow model 和 jobModel list
 *
 * @author liangyongrui@xiaomi.com
 */
class FlowAllInfoRp(
        val flowModel: FlowDr,
        val jobModelList: List<JobModel>
)
