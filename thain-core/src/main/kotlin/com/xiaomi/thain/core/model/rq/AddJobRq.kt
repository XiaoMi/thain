package com.xiaomi.thain.core.model.rq

import com.alibaba.fastjson.JSON
import com.xiaomi.thain.core.model.dr.JobDr

/**
 * Date 19-5-20 下午9:00
 *
 * @author liangyongrui@xiaomi.com
 */
data class AddJobRq(
        val name: String,
        val condition: String?,
        val component: String?,
        val callbackUrl: String?,
        val properties: Map<String, String>,
        val xAxis: Int?,
        val yAxis: Int?
) {

    constructor(jobDr: JobDr) : this(
            jobDr.name,
            jobDr.condition,
            jobDr.component,
            jobDr.callbackUrl,
            jobDr.properties,
            jobDr.xAxis,
            jobDr.yAxis
    )

    val propertiesString: String
        get() = JSON.toJSONString(properties)
}
