/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.server.model.ThainUser;
import com.xiaomi.thain.server.model.dr.X5ConfigDr;
import com.xiaomi.thain.server.model.rp.UserResponse;
import com.xiaomi.thain.server.model.rp.X5ConfigResponse;
import com.xiaomi.thain.server.model.rq.AddUserRq;
import com.xiaomi.thain.server.model.rq.UpdateUserRq;
import com.xiaomi.thain.server.model.rq.X5ConfigRq;
import com.xiaomi.thain.server.service.UserService;
import com.xiaomi.thain.server.service.X5Service;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.xiaomi.thain.server.handler.ThreadLocalUser.isAdmin;

/**
 * @author wangsimin3@xiaomi.com
 */
@Log4j2
@RestController
@RequestMapping("api/admin")
public class AdminController {

    @NonNull
    private final UserService userService;

    @NonNull
    private final X5Service x5Service;

    public AdminController(@NonNull UserService userService, @NonNull X5Service x5Service) {
        this.userService = userService;
        this.x5Service = x5Service;
    }

    @GetMapping("/users")
    public ApiResult getAllUserInfo(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        if (isAdmin()) {
            PageMethod.startPage(page, pageSize);
            PageInfo<ThainUser> thainUserPageInfo = new PageInfo<>(userService.getAllUsers());
            return ApiResult.success(thainUserPageInfo.getList().stream().map(t -> UserResponse.builder()
                            .userId(t.getUserId())
                            .userName(t.getUsername())
                            .admin(t.isAdmin())
                            .email(t.getEmail())
                            .build()).collect(Collectors.toList()),
                    thainUserPageInfo.getTotal(),
                    thainUserPageInfo.getPageNum(),
                    thainUserPageInfo.getPageSize());
        }
        return ApiResult.fail("Not allow to access");
    }

    @DeleteMapping("/user/{userId}")
    public ApiResult deleteUser(@PathVariable("userId") @NonNull String userId) {
        if (isAdmin()) {
            userService.deleteUser(userId);
            return ApiResult.success();
        }
        return ApiResult.fail("Not Access ");
    }

    @PostMapping("/user")
    public ApiResult addUser(@RequestBody @NonNull AddUserRq addUserRq) {
        if (isAdmin()) {
            if (userService.insertUser(addUserRq)) {
                return ApiResult.success();
            }
            return ApiResult.fail("userId has existed ");
        }
        return ApiResult.fail("Not Access to delete user");
    }

    @PatchMapping("/user")
    public ApiResult updateUser(@RequestBody @NonNull UpdateUserRq request) {
        if (isAdmin()) {
            if (userService.updateUser(request)) {
                return ApiResult.success();
            }
            return ApiResult.fail("UserId is not existed");
        }
        return ApiResult.fail("Not Access to update user");
    }

    @GetMapping("/clients")
    public ApiResult getAllConfigs(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        if (isAdmin()) {
            PageMethod.startPage(page, pageSize);
            val pageInfo = new PageInfo<X5ConfigDr>(x5Service.getAllConfigs());
            val x5Configs = new ArrayList<X5ConfigResponse>();
            pageInfo.getList().forEach(x5ConfigDr -> {
                val principals = JSON.parseArray(x5ConfigDr.principal, String.class);
                x5Configs.add(X5ConfigResponse.builder()
                        .appId(x5ConfigDr.appId)
                        .appKey(x5ConfigDr.appKey)
                        .appName(x5ConfigDr.appName)
                        .description(x5ConfigDr.description)
                        .createTime(x5ConfigDr.createTime.getTime())
                        .principals(principals).build());
            });
            return ApiResult.success(x5Configs, pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        return ApiResult.fail("Not Access to get clients");
    }

    @DeleteMapping("/client/{appId}")
    public ApiResult deleteConfig(@PathVariable("appId") @NonNull String appId) {
        if (isAdmin()) {
            x5Service.deleteX5Config(appId);
            return ApiResult.success();
        }
        return ApiResult.fail("Not Access to delete x5config");
    }

    @PostMapping("/client")
    public ApiResult addConfig(@RequestBody @NonNull X5ConfigRq x5ConfigRq) {
        if (isAdmin()) {
            if (x5Service.insertX5Config(x5ConfigRq)) {
                return ApiResult.success();
            }
            return ApiResult.fail("AppId existed or principal is null ");
        }
        return ApiResult.fail("Not Allow to Access");
    }

    @PatchMapping("/client")
    public ApiResult updateConfig(@RequestBody @NonNull X5ConfigRq x5ConfigRq) {
        if (isAdmin()) {
            if (x5Service.updateX5Config(x5ConfigRq)) {
                return ApiResult.success();
            }
            return ApiResult.fail("X5config has been delete");
        }
        return ApiResult.fail("Not Allow to update");
    }
}
