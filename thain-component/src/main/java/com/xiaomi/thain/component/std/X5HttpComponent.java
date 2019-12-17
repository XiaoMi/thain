/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.component.std;

import com.alibaba.fastjson.JSON;
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
@ThainComponent("{\"group\": \"std\", \"name\": \"x5http\", \"hidden\": true, \"items\": [{\"property\": \"url\", \"label\": \"HTTP URL\", \"required\": true, \"input\": {\"id\": \"textarea\"}}, {\"property\": \"referenceData\", \"label\": \"流程数据引用\", \"input\": {\"id\": \"textarea\"}}, {\"property\": \"resultRegular\", \"label\": \"结果正则\", \"input\": {\"id\": \"textarea\"}}]}\n")
@SuppressWarnings("unused")
public class X5HttpComponent {
    /**
     * 流程执行工具
     */
    private ComponentTools tools;
    /**
     * http url
     */
    private String url;

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
        val result = tools.httpX5Post(url, data);
        tools.putStorage("result", result);
        tools.addInfoLog(result);
        if (StringUtils.isNotBlank(resultRegular) && !result.matches(resultRegular)) {
            throw new ThainException("Request result not satisfied regular expression: " + resultRegular);
        }
    }

}
