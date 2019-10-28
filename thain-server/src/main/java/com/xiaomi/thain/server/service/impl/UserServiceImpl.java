/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service.impl;

import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.server.dao.UserDao;
import com.xiaomi.thain.server.entity.request.UserRequest;
import com.xiaomi.thain.server.entity.rq.UserRq;
import com.xiaomi.thain.server.entity.user.ThainUser;
import com.xiaomi.thain.server.service.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ThainUser getUserById(@NonNull String userId) {
        val user = userDao.getUserById(userId);
        return user.orElseThrow(() -> new ThainRuntimeException("user does not exist"));
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
    public boolean insertUser(@NonNull UserRequest userRequest) {
        if (!userDao.getUserById(userRequest.userId).isPresent()) {
            userDao.insertUser(userRequest);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUser(@NonNull UserRq userRq) {
        return userDao.updateUser(userRq);
    }

}

