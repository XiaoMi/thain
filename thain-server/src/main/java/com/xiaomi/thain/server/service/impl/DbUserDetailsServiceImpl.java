/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.service.impl;

import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.server.dao.UserDao;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


/**
 * @author miaoyu
 */
@Service("DbUserDetailsService")
public class DbUserDetailsServiceImpl implements UserDetailsService {

    @NonNull
    private final UserDao userDao;

    public DbUserDetailsServiceImpl(@NonNull UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) {
        return userDao.getUserById(userId).orElseThrow(() -> new ThainRuntimeException("user does not exist"));
    }
}
