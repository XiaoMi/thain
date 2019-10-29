/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service;

import com.xiaomi.thain.server.model.rq.AddUserRq;
import com.xiaomi.thain.server.model.rq.UpdateUserRq;
import com.xiaomi.thain.server.model.ThainUser;
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
     * @param addUserRq see {@link AddUserRq}
     * @return false-failure true-success
     */
    boolean insertUser(@NonNull AddUserRq addUserRq);

    /**
     * update user By admin
     *
     * @param updateUserRq see {@link UpdateUserRq}
     * @return false-failure true-success
     */
    boolean updateUser(@NonNull UpdateUserRq updateUserRq);
}

