/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.common.entity;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author liangyongrui
 */
@Builder
@ToString
@AllArgsConstructor
public class ApiResult {
    public final int status;
    @NonNull
    public final String message;
    @Nullable
    public final Object data;

    public static ApiResult success() {
        return success("");
    }

    public static ApiResult success(@NonNull List<?> data, long count, int page, int pageSize) {
        return new ApiResult(200, "success", ImmutableMap.of(
                "data", data,
                "count", count,
                "page", page,
                "pageSize", pageSize
        ));
    }

    public static ApiResult success(@Nullable Object data) {
        return new ApiResult(200, "success", data);
    }

    public static ApiResult fail(@Nullable String message) {
        return new ApiResult(400, message == null ? "unknown error" : message, "");
    }
}

