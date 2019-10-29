/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.dao;

import com.xiaomi.thain.server.model.rq.AddUserRq;
import com.xiaomi.thain.server.model.rq.UpdateUserRq;
import com.xiaomi.thain.server.model.ThainUser;
import com.xiaomi.thain.server.mapper.UserMapper;
import lombok.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author miaoyu
 */
@Repository
public class UserDao {

    @NonNull
    private final UserMapper userMapper;

    public UserDao(@NonNull UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Optional<ThainUser> getUserById(@NonNull String userId) {
        return userMapper.getUserById(userId);
    }

    public void insertUser(@NonNull AddUserRq addUserRq) {
        ThainUser insertUser = new ThainUser();
        insertUser.setUserId(addUserRq.userId);
        insertUser.setUsername(addUserRq.username);
        insertUser.setPasswordHash(new BCryptPasswordEncoder().encode(addUserRq.password));
        userMapper.insertUser(insertUser);
    }

    public void insertThirdUser(@NonNull ThainUser user) {
        ThainUser insertUser = ThainUser.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .passwordHash("thain")
                .build();
        userMapper.insertUser(insertUser);
    }

    public void deleteUser(@NonNull String userId) {
        userMapper.deleteUser(userId);
    }

    public List<ThainUser> getAllUsers() {
        return userMapper.getAllUsers();
    }

    public boolean updateUser(@NonNull UpdateUserRq updateUserRq) {
        if (userMapper.getUserById(updateUserRq.userId).isPresent()) {
            userMapper.updateUserBySelective(updateUserRq);
            return true;
        }
        return false;
    }


}
