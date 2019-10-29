/*
 * Copyright (c) ${YEAR}, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.entity.rq;

import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nullable;

/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/28
 */
@Builder
public class UserRq {
    @NonNull
    public final String userId;
    public final boolean admin;
    @Nullable
    public final String email;
    @Nullable
    public final String username;
    @Nullable
    public final String password;
}
