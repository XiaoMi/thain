/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.component.std;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.component.annotation.ThainComponent;
import com.xiaomi.thain.component.tools.ComponentTools;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * Date 19-5-16 下午8:48
 *
 * @author liangyongrui@xiaomi.com
 */
@ThainComponent(group = "std", name = "http",
        defineJson = "[{\"property\": \"url\", \"label\": \"HTTP URL\", \"required\": true, \"input\": {\"id\": \"textarea\"}}, {\"property\": \"method\", \"label\": \"HTTP Method\", \"required\": true, \"input\": {\"id\": \"select\", \"options\": [{\"id\": \"GET\"}, {\"id\": \"POST\"}]}}, {\"property\": \"contentType\", \"label\": \"Content-Type\", \"input\": {\"id\": \"select\", \"options\": [{\"id\": \"application/json\"}, {\"id\": \"application/x-www-form-urlencoded\"}]}}, {\"property\": \"referenceData\", \"label\": \"流程数据引用, 多个用逗号分开, 如: `aName:a.name,bAge:b.age`, 冒号前面的是http发过去的key, 后面是value\", \"input\": {\"id\": \"textarea\"}}, {\"property\": \"forwardData\", \"label\": \"转发数据\", \"input\": {\"id\": \"textarea\"}}]")
@SuppressWarnings("unused")
public class HttpComponent {
    /**
     * 流程执行工具
     */
    private ComponentTools tools;
    /**
     * http url
     */
    private String url;

    /**
     * http method
     * GET or POST
     */
    private String method;

    /**
     * Content-Type
     */
    private String contentType;

    /**
     * 流程数据引用，引用的key用空格分开
     */
    private String referenceData;

    /**
     * forwardData
     */
    private String forwardData;

    @SuppressWarnings("unused")
    private void run() throws ThainException {

        val data = new HashMap<String, String>(16);
        if (Objects.nonNull(forwardData)) {
            data.put("forwardData", forwardData);
        }
        if (Objects.nonNull(referenceData)) {
            Arrays.stream(referenceData.split(",")).map(String::trim).forEach(t -> {
                val key = t.split("[:.]");
                if (key.length == 3) {
                    val value = String.valueOf(tools.getStorageValueOrDefault(key[1].trim(), key[2].trim(), ""));
                    data.put(key[0].trim(), value);
                }
            });
        }
        tools.addDebugLog(JSON.toJSONString(data));
        String result;
        try {
            switch (method.toUpperCase()) {
                case "GET":
                    tools.addDebugLog("GET request");
                    result = tools.httpGet(url, data);
                    tools.addDebugLog("Request completed");
                    break;
                case "POST":
                    tools.addDebugLog("POST request");
                    if (StringUtils.isBlank(contentType)) {
                        contentType = "application/json;charset=UTF-8";
                    }
                    tools.addDebugLog("Content-Type: " + contentType);
                    result = tools.httpPost(url, ImmutableMap.of("Content-Type", contentType), data);
                    tools.addDebugLog("Request completed");
                    break;
                default:
                    throw new ThainException("can not support this method:" + method);
            }
        } catch (Exception e) {
            throw new ThainException(e);
        }
        tools.putStorage("result", result);
        tools.addInfoLog(result);
    }

}
