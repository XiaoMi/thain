/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service;

import com.xiaomi.thain.server.dao.UserDao;
import com.xiaomi.thain.server.model.ThainUser;
import com.xiaomi.thain.server.model.rq.AddUserRq;
import com.xiaomi.thain.server.model.rq.UpdateUserRq;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author miaoyu3@xiaomi.com
 * @date 19-8-7下午2:03
 */
@Slf4j
@Service
public class UserService {
    @NonNull
    private final UserDao userDao;

    public UserService(@NonNull UserDao userDao) {
        this.userDao = userDao;
    }

    public void insertThirdUser(@NonNull ThainUser thainUser) {
        userDao.insertThirdUser(thainUser);
    }

    public ThainUser getUserById(@NonNull String userId) {
        return userDao.getUserById(userId);
    }

    public void deleteUser(@NonNull String userId) {
        userDao.deleteUser(userId);
    }

    public List<ThainUser> getAllUsers() {
        return userDao.getAllUsers();
    }

    public boolean insertUser(@NonNull AddUserRq addUserRq) {
        if (userDao.getUserById(addUserRq.userId) == null) {
            userDao.insertUser(addUserRq);
            return true;
        }
        return false;
    }

    public boolean updateUser(@NonNull UpdateUserRq updateUserRq) {
        return userDao.updateUser(updateUserRq);
    }

}

