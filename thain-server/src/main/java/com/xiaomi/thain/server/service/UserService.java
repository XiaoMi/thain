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
    /**
     * Insert third-party user
     *
     * @param thainUser see {@link ThainUser}
     */
    void insertThirdUser(@NonNull ThainUser thainUser);

    /**
     * get ThainUser By Id
     *
     * @param userId userId
     * @return see {@link ThainUser}
     */
    ThainUser getUserById(@NonNull String userId);

    /**
     * delete user
     *
     * @param userId userId
     */
    void deleteUser(@NonNull String userId);

    /**
     * get all thain user
     *
     * @return see {@link ThainUser}
     */
    List<ThainUser> getAllUsers();

    /**
     * insert user By admin
     *
     * @param userRequest see {@link UserRequest}
     * @return false-failure true-success
     */
    boolean insertUser(@NonNull UserRequest userRequest);

    /**
     * update user By admin
     *
     * @param userRq see {@link UserRq}
     * @return false-failure true-success
     */
    boolean updateUser(@NonNull UserRq userRq);
}

