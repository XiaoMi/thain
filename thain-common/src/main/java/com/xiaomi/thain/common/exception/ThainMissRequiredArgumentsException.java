/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.exception;

import lombok.NonNull;

/**
 * Date 19-5-31 下午8:10
 *
 * @author liangyongrui@xiaomi.com
 */
public class ThainMissRequiredArgumentsException extends ThainException {
    public ThainMissRequiredArgumentsException(@NonNull String message) {
        super(message);
    }

    public ThainMissRequiredArgumentsException() {
    }

    public ThainMissRequiredArgumentsException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }

    public ThainMissRequiredArgumentsException(@NonNull Throwable cause) {
        super(cause);
    }

    public ThainMissRequiredArgumentsException(@NonNull String message, @NonNull Throwable cause,
                                               boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
