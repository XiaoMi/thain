package com.xiaomi.thain.server.controller;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.server.entity.request.UserRequest;
import com.xiaomi.thain.server.entity.request.X5ConfigRequest;
import com.xiaomi.thain.server.entity.response.UserResponse;
import com.xiaomi.thain.server.entity.response.X5ConfigResponse;
import com.xiaomi.thain.server.entity.rq.UserRq;
import com.xiaomi.thain.server.entity.user.ThainUser;
import com.xiaomi.thain.server.service.UserService;
import com.xiaomi.thain.server.service.X5Service;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static com.xiaomi.thain.server.handler.ThreadLocalUser.isAdmin;

/**
 * @author wangsimin3@xiaomi.com
 */
@Slf4j
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
    public ApiResult addUser(@RequestBody @NonNull UserRequest userRequest) {
        if (isAdmin()) {
            if (userService.insertUser(userRequest)) {
                return ApiResult.success();
            }
            return ApiResult.fail("userId has existed ");
        }
        return ApiResult.fail("Not Access to delete user");
    }

    @PatchMapping("/user")
    public ApiResult updateUser(@RequestBody @NonNull UserRq request) {
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
            val pageInfo = new PageInfo<X5ConfigResponse>(PageMethod.startPage(page, pageSize));
            return ApiResult.success(x5Service.getAllConfigs(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
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
    public ApiResult addConfig(@RequestBody @NonNull X5ConfigRequest x5ConfigRequest) {
        if (isAdmin()) {
            if (x5Service.insertX5Config(x5ConfigRequest)) {
                return ApiResult.success();
            }
            return ApiResult.fail("AppId existed or principal is null ");
        }
        return ApiResult.fail("Not Allow to Access");
    }

    @PatchMapping("/client")
    public ApiResult updateConfig(@RequestBody @NonNull X5ConfigRequest x5ConfigRequest) {
        if (isAdmin()) {
            if (x5Service.updateX5Config(x5ConfigRequest)) {
                return ApiResult.success();
            }
            return ApiResult.fail("X5config has been delete");
        }
        return ApiResult.fail("Not Allow to update");
    }
}
