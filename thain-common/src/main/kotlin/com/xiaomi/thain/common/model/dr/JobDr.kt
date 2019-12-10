package com.xiaomi.thain.common.model.dr

/**
 * Date 19-5-20 下午9:00
 *
 * @author liangyongrui@xiaomi.com
 */
class JobDr(val id: Long,
            val flowId: Long,
            val name: String,
            val condition: String,
            val component: String,
            val callbackUrl: String?,
            val properties: String,
            val xAxis: Int,
            val yAxis: Int,
            val createTime: java.sql.Timestamp,
            val deleted: Boolean
) {
    val propertiesMap: com.alibaba.fastjson.JSONObject
        get() = com.alibaba.fastjson.JSON.parseObject(properties)
}