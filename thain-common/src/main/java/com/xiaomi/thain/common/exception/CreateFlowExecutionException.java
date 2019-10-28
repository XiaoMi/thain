/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.exception;

import lombok.NonNull;

import javax.annotation.Nullable;

/**
 * Date 19-5-17 下午1:50
 *
 * @author liangyongrui@xiaomi.com
 */
public class CreateFlowExecutionException extends ThainException {

    public CreateFlowExecutionException(long flowId, @Nullable String message) {
        super("Failed to create flow, flowId:" + flowId + ", message:" + message);
    }

    public CreateFlowExecutionException() {
        super("Failed to create flow");
    }

    public CreateFlowExecutionException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }

    public CreateFlowExecutionException(@NonNull Throwable cause) {
        super(cause);
    }

    public CreateFlowExecutionException(@NonNull String message, @NonNull Throwable cause,
                                        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
