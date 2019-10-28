/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.constant;

/**
 * Date 19-5-21 上午10:23
 * http 回调状态
 *
 * @author liangyongrui@xiaomi.com
 */
public enum HttpCallbackStatus {
    /**
     * 1 开始运行
     */
    START(1),
    /**
     * 2 运行成功
     */
    SUCCESS(2),
    /**
     * 3 运行异常
     */
    ERROR(3),
    /**
     * 4 手动杀死
     */
    KILLED(4),
    /**
     * 5 暂停运行
     */
    PAUSE(5);

    public final int code;

    HttpCallbackStatus(int code) {
        this.code = code;
    }
}
