/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.constant;

/**
 * JobExecution的执行状态
 *
 * @author liangyongrui
 */
public enum JobExecutionStatus {
    /**
     * 1 未运行
     */
    NEVER(1),
    /**
     * 2 正在运行
     */
    RUNNING(2),
    /**
     * 3 执行成功
     */
    SUCCESS(3),
    /**
     * 4 执行异常
     */
    ERROR(4);

    public final int code;

    JobExecutionStatus(int code) {
        this.code = code;
    }

}
