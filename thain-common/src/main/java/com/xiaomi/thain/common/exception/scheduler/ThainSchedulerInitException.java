/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.exception.scheduler;

import lombok.NonNull;

/**
 * Date 19-5-17 下午1:50
 * 调度器初始化失败
 *
 * @author liangyongrui@xiaomi.com
 */
public class ThainSchedulerInitException extends ThainSchedulerException {
    public ThainSchedulerInitException(@NonNull String message) {
        super(message);
    }

    public ThainSchedulerInitException() {
    }

    public ThainSchedulerInitException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }

    public ThainSchedulerInitException(@NonNull Throwable cause) {
        super(cause);
    }

    public ThainSchedulerInitException(@NonNull String message, @NonNull Throwable cause,
                                       boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
