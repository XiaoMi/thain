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

import javax.annotation.Nullable;
import java.util.HashMap;

import static com.xiaomi.thain.component.util.GlobalKt.formatHttpReferenceData;

/**
 * Date 19-5-16 下午8:48
 *
 * @author liangyongrui@xiaomi.com
 */
@ThainComponent("{\"group\": \"std\", \"name\": \"http\", \"hidden\": false, \"items\": [{\"property\": \"url\", \"label\": \"HTTP URL\", \"required\": true, \"input\": {\"id\": \"textarea\"}}, {\"property\": \"method\", \"label\": \"HTTP Method\", \"required\": true, \"input\": {\"id\": \"select\", \"options\": [{\"id\": \"GET\"}, {\"id\": \"POST\"}]}}, {\"property\": \"contentType\", \"label\": \"Content-Type\", \"input\": {\"id\": \"select\", \"options\": [{\"id\": \"application/json\"}, {\"id\": \"application/x-www-form-urlencoded\"}]}}, {\"property\": \"referenceData\", \"label\": \"流程数据引用\", \"input\": {\"id\": \"textarea\"}}, {\"property\": \"forwardData\", \"label\": \"转发数据\", \"input\": {\"id\": \"textarea\"}}, {\"property\": \"resultRegular\", \"label\": \"结果正则\", \"input\": {\"id\": \"textarea\"}}]}\n")
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
    @Nullable
    private String contentType;

    /**
     * 流程数据引用，引用的key用空格分开
     */
    @Nullable
    private String referenceData;

    /**
     * forwardData
     */
    @Nullable
    private String forwardData;

    /**
     * 结果正则表达式
     */
    @Nullable
    private String resultRegular;

    @SuppressWarnings("unused")
    private void run() throws ThainException {

        val data = new HashMap<String, String>(16);
        if (StringUtils.isNotBlank(forwardData)) {
            data.put("forwardData", forwardData);
        }
        data.putAll(formatHttpReferenceData(referenceData, tools::getStorageValueOrDefault));
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
        if (StringUtils.isNotBlank(resultRegular) && !result.matches(resultRegular)) {
            throw new ThainException("Request result not satisfied regular expression: " + resultRegular);
        }
    }

}
