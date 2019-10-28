/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service;

import com.xiaomi.thain.server.entity.request.UserRequest;
import com.xiaomi.thain.server.entity.rq.UserRq;
import com.xiaomi.thain.server.entity.user.ThainUser;
import lombok.NonNull;

import java.util.List;

/**
 * @author miaoyu3@xiaomi.com
 * @date 19-8-7下午2:03
 */
public interface UserService {
    void insertThirdUser(@NonNull ThainUser thainUser);

    ThainUser getUserById(@NonNull String userId);

    void deleteUser(@NonNull String userId);

    List<ThainUser> getAllUsers();

    boolean insertUser(@NonNull UserRequest userRequest);
    boolean updateUser(@NonNull UserRq userRq);
}

