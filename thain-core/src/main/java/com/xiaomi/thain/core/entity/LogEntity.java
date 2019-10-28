/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.entity;

import lombok.Builder;

import javax.annotation.Nullable;

/**
 * 日志节点，一个日志是由多个日志节点组成
 *
 * @author liangyongrui@xiaomi.com
 */
@Builder
public class LogEntity {

    /**
     * 毫秒时间戳
     */
    @Nullable
    public final Long timestamp;

    /**
     * 日志等级，com.xiaomi.thain.core.constant.LogLevel
     */
    @Nullable
    public final String level;

    /**
     * 具体内容
     */
    @Nullable
    public final String content;
}
