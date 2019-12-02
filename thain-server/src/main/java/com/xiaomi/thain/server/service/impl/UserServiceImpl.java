/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service.impl;

import com.xiaomi.thain.server.dao.UserDao;
import com.xiaomi.thain.server.model.ThainUser;
import com.xiaomi.thain.server.model.rq.AddUserRq;
import com.xiaomi.thain.server.model.rq.UpdateUserRq;
import com.xiaomi.thain.server.service.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author miaoyu3@xiaomi.com
 * @date 19-8-7下午2:03
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @NonNull
    private final UserDao userDao;

    public UserServiceImpl(@NonNull UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void insertThirdUser(@NonNull ThainUser thainUser) {
        userDao.insertThirdUser(thainUser);
    }

    @Override
    public Optional<ThainUser> getUserById(@NonNull String userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public void deleteUser(@NonNull String userId) {
        userDao.deleteUser(userId);
    }

    @Override
    public List<ThainUser> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public boolean insertUser(@NonNull AddUserRq addUserRq) {
        if (!userDao.getUserById(addUserRq.userId).isPresent()) {
            userDao.insertUser(addUserRq);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUser(@NonNull UpdateUserRq updateUserRq) {
        return userDao.updateUser(updateUserRq);
    }

}

