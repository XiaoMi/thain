/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.constant;

/**
 * flow的调度状态
 *
 * @author liangyongrui
 */
public enum FlowSchedulingStatus {
    /**
     * 1 调度中
     */
    SCHEDULING(1),
    /**
     * 2 暂停调度
     */
    PAUSE(2),

    /**
     * 3 未设置调度（只运行一次的任务）
     */
    NOT_SET(3);

    public final int code;

    FlowSchedulingStatus(int code) {
        this.code = code;
    }

}
