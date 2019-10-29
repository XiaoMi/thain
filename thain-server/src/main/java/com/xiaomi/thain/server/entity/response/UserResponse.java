/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.entity.response;

import lombok.*;


/**
 * @author  wangsimin3@xiaomi.com
 */

@Builder
public class UserResponse   {
   @NonNull
   public final String userId;
   public final boolean admin;
   public final String email;
   public final String userName;
}
