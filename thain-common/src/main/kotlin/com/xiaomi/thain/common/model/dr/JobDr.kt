package com.xiaomi.thain.common.model.dr

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import java.sql.Timestamp

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
            val createTime: Timestamp,
            val deleted: Boolean
) {
    val propertiesMap: JSONObject
        get() = JSON.parseObject(properties)
}
