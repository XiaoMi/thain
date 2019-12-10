package com.xiaomi.thain.common.model.rq

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
            com.alibaba.fastjson.JSON.parseObject(jobDr.properties, object : com.alibaba.fastjson.TypeReference<Map<String, String>>() {}),
            jobDr.xAxis,
            jobDr.yAxis
    )

    val propertiesString: String
        get() = com.alibaba.fastjson.JSON.toJSONString(properties)
}