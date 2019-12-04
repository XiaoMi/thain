/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.common.constant;

/**
 * flow execution 的执行状态
 *
 * @author liangyongrui
 */
public enum FlowExecutionStatus {

    /**
     * 0 等待运行
     */
    WAITING(0),
    /**
     * 1 正在运行
     */
    RUNNING(1),
    /**
     * 2 运行成功
     */
    SUCCESS(2),
    /**
     * 3 运行异常
     */
    ERROR(3),

    /**
     * 4 手动kill
     */
    KILLED(4),

    /**
     * 5 禁止同时运行一个flow
     */
    DO_NOT_RUN_SAME_TIME(5),

    /**
     * 自动kill
     */
    AUTO_KILLED(6);


    public final int code;

    FlowExecutionStatus(int code) {
        this.code = code;
    }

    public static FlowExecutionStatus getInstance(int status) {
        switch (status) {
            case 0:
                return WAITING;
            case 1:
                return RUNNING;
            case 2:
                return SUCCESS;
            case 3:
                return ERROR;
            case 4:
                return KILLED;
            case 5:
                return DO_NOT_RUN_SAME_TIME;
            case 6:
                return AUTO_KILLED;

            default:
        }
        return RUNNING;
    }
}
