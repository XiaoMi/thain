/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.exception;

import lombok.NonNull;

/**
 * Date 19-5-17 下午1:50
 * Json 验证异常
 *
 * @author liangyongrui@xiaomi.com
 */
public class JsonValidateException extends ThainException {
    public JsonValidateException(@NonNull String message) {
        super(message);
    }

    public JsonValidateException() {
    }

    public JsonValidateException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }

    public JsonValidateException(@NonNull Throwable cause) {
        super(cause);
    }

    public JsonValidateException(@NonNull String message, @NonNull Throwable cause,
                                 boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
