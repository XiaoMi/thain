/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.entity.request;


import lombok.Builder;
import lombok.NonNull;

/**
 * @author wangsimin@xiaomi.com
 */
@Builder
public class UserRequest {
    @NonNull
    public final String userId;
    public final boolean admin;
    @NonNull
    public final String email;
    @NonNull
    public final String username;
    @NonNull
    public final String password;
}
