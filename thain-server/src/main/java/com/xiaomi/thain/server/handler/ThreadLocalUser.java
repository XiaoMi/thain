/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.handler;

import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.server.model.ThainUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author liangyongrui@xiaomi.com
 * @date 11/8/18 3:47 PM
 */
@Slf4j
public class ThreadLocalUser {

    private ThreadLocalUser() {
    }

    public static Optional<ThainUser> getThainUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(user -> (ThainUser) user);
    }

    /**
     * 获取当前用户id
     */
    public static String getUsername() throws ThainException {
        return getThainUser().map(ThainUser::getUserId).orElseThrow(() -> new ThainException("Failed to obtain logged-in user"));
    }

    public static boolean isAdmin() {
        return getThainUser().map(ThainUser::isAdmin).orElse(false);
    }

    public static Set<String> getAuthorities() {
        return getThainUser().map(ThainUser::getAppIds).orElseGet(Collections::emptySet);
    }


}
