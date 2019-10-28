/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Date 19-5-20 下午9:00
 *
 * @author liangyongrui@xiaomi.com
 */
@Builder(toBuilder = true)
@AllArgsConstructor
public class JobModel {
    public final long id;
    public final long flowId;
    @Nullable
    public final String name;
    @Nullable
    public final String condition;
    @Nullable
    public final String component;
    @Nullable
    public final String callbackUrl;
    @Nullable
    public final Map<String, Object> properties;
    public final int xAxis;
    public final int yAxis;
    @Nullable
    public final Timestamp createTime;
    public final boolean deleted;

    public Optional<Long> getCreateTime() {
        return Optional.ofNullable(createTime).map(Timestamp::getTime);
    }

    public JobModel(long id,
                    long flowId,
                    @Nullable String name,
                    @Nullable String condition,
                    @Nullable String component,
                    @Nullable String callbackUrl,
                    @NonNull String propertiesJson,
                    int xAxis,
                    int yAxis,
                    @Nullable Timestamp createTime,
                    boolean deleted
    ) {
        this.id = id;
        this.flowId = flowId;
        this.name = name;
        this.condition = condition;
        this.component = component;
        this.callbackUrl = callbackUrl;
        this.properties = ImmutableMap.copyOf(JSON.parseObject(propertiesJson));
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.createTime = createTime;
        this.deleted = deleted;
    }

    /**
     * mybatis 使用
     */
    public String getPropertiesString() {
        return JSON.toJSONString(properties);
    }
}
