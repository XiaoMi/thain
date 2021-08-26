/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.utils

import com.alibaba.fastjson.JSON
import com.xiaomi.thain.common.entity.X5
import org.apache.commons.codec.digest.DigestUtils
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @author liangyongrui
 */
object X5Utils {
    private val DECODER = Base64.getDecoder()
    private val ENCODER = Base64.getEncoder()
    fun buildX5Request(appid: String, key: String, body: Map<String, String>): Map<String, String> {
        return buildX5Request(appid, key, JSON.toJSONString(body))
    }

    @JvmStatic
    fun buildX5Request(appid: String, key: String, jsonBody: String): Map<String, String> {
        val sign = DigestUtils.md5Hex((appid + jsonBody + key).toByteArray(StandardCharsets.UTF_8)).uppercase()
        val header: MutableMap<String, String> = HashMap(4)
        header["appid"] = appid
        header["sign"] = sign
        val data: Map<*, *> = mapOf("header" to header, "body" to jsonBody)
        val dataStr = JSON.toJSONString(data)
        var base64 = String(ENCODER.encode(dataStr.toByteArray(StandardCharsets.UTF_8)))
        base64 = base64.replace("\n".toRegex(), "")
        return mapOf("data" to base64)
    }

    @JvmStatic
    fun getX5(data: String): X5 {
        val dataString = String(DECODER.decode(data), StandardCharsets.UTF_8)
        return JSON.parseObject(dataString, X5::class.java)
    }

    @JvmStatic
    fun validate(x5: X5, appkey: String): Boolean {
        val md5String = DigestUtils.md5Hex((x5.header.appid + x5.body + appkey).toByteArray(StandardCharsets.UTF_8))
            .uppercase()
        return md5String == x5.header.sign
    }
}