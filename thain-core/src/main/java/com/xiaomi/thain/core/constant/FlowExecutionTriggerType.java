/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.constant;

import lombok.val;

/**
 * flowExecution触发类型
 *
 * @author liangyongrui
 */
public enum FlowExecutionTriggerType {
    /**
     * 自动重试
     */
    RETRY(3),
    /**
     * 自动触发
     */
    AUTOMATIC(2),
    /**
     * 手动触发
     */
    MANUAL(1);

    public final int code;

    FlowExecutionTriggerType(int code) {
        this.code = code;
    }

    public static FlowExecutionTriggerType getInstance(int code) {
        for (val t : FlowExecutionTriggerType.values()) {
            if (t.code == code) {
                return t;
            }
        }
        return MANUAL;
    }
}
