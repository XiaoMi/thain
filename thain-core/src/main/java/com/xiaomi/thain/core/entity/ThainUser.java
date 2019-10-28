/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.entity;

import lombok.AllArgsConstructor;

import javax.annotation.Nullable;

/**
 * Date 2019/8/9 下午3:42
 *
 * @author liangyongrui@xiaomi.com
 */
@AllArgsConstructor
public class ThainUser {
    public final long id;
    @Nullable
    public final String userId;
    @Nullable
    public final String userName;
    @Nullable
    public final String passwordHash;
    @Nullable
    public final String email;
    public final boolean admin;
}
