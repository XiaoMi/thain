/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model.rq.kt

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

/**
 * Date 19-5-20 下午9:00
 *
 * @author liangyongrui@xiaomi.com
 */
data class AddJobRq(
//        val id: Long,
//        val flowId: Long,
        val name: String,
        val condition: String?,
        val component: String?,
        val callbackUrl: String?,
        val properties: String,
        val xAxis: Int?,
        val yAxis: Int?
) {
    fun getPropertiesMap(): JSONObject {
        return JSON.parseObject(properties)
    }
}
