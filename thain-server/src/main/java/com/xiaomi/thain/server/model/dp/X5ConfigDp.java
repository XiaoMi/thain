/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.model.dp;

import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nullable;


/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/25
 */
@Builder
public class X5ConfigDp {
    @NonNull
    public final String appId;
    @NonNull
    public final String appKey;
    @NonNull
    public final String appName;
    @Nullable
    public final String description;
    @NonNull
    public final String principal;
}
