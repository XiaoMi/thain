/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.entity.dr;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.sql.Timestamp;

/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/29
 */
@AllArgsConstructor
public class X5ConfigDr {
    @NonNull
    public final String appId;
    @NonNull
    public final String appKey;
    @NonNull
    public final String appName;
    @NonNull
    public final String principal;
    @Nullable
    public final String description;
    @NonNull
    public final Timestamp createTime;
}
