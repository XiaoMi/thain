package com.xiaomi.thain.core.model.dp

import com.xiaomi.thain.core.model.rq.AddJobRq

/**
 * @author liangyongrui
 */
class AddJobDp (
    val flowId: Long,
    val name: String,
    val condition: String?,
    val component: String?,
    val callbackUrl: String?,
    val properties: String?,
    val xAxis: Int?,
    val yAxis: Int?
){
    companion object {
        fun getInstance(addJobRq: AddJobRq, flowId: Long): AddJobDp {
            return AddJobDp(flowId,
                    addJobRq.name,
                    addJobRq.condition,
                    addJobRq.component,
                    addJobRq.callbackUrl,
                    addJobRq.propertiesString,
                    addJobRq.xAxis,
                    addJobRq.yAxis)
        }
    }
}
