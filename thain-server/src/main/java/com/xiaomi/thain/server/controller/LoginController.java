/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.controller;

import com.google.common.collect.ImmutableMap;
import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.common.exception.ThainException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static com.xiaomi.thain.server.handler.ThreadLocalUser.getThainUser;

/**
 * @author liangyongrui
 */
@Log4j2
@RestController
@RequestMapping("api/login")
public class LoginController {

    @GetMapping("current-user")
    public ApiResult currentUser() {
        try {
            val user = getThainUser().orElseThrow(() -> new ThainException("user does not exist"));
            return ApiResult.success(ImmutableMap.of(
                    "userId", user.getUserId(),
                    "name", user.getUsername(),
                    "authority", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray()
            ));
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
    }

    @GetMapping("logout")
    public ApiResult logout(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (Objects.nonNull(auth)) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            return ApiResult.success();
        } catch (Exception e) {
            return ApiResult.fail("logout failed");
        }
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(@NonNull CsrfToken token) {
        return token;
    }
}
