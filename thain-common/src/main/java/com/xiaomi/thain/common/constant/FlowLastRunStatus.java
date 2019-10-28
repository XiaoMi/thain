/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.common.constant;

import lombok.val;

/**
 * flow的最后一次运行状态
 *
 * @author liangyongrui
 */
public enum FlowLastRunStatus {
    /**
     * 1 未运行
     */
    NEVER(1),
    /**
     * 2 运行成功
     */
    SUCCESS(2),
    /**
     * 3 运行异常
     */
    ERROR(3),
    /**
     * 4 正在运行
     */
    RUNNING(4),
    /**
     * 5 手动杀死
     */
    KILLED(5),
    /**
     * 6 暂停运行
     */
    PAUSE(6);

    public final int code;

    FlowLastRunStatus(int code) {
        this.code = code;
    }

    public static FlowLastRunStatus getInstance(int code) {
        for (val t : FlowLastRunStatus.values()) {
            if (t.code == code) {
                return t;
            }
        }
        return SUCCESS;
    }
}
