package com.xiaomi.thain.server.controller;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.server.entity.request.UserRequest;
import com.xiaomi.thain.server.entity.response.UserResponse;
import com.xiaomi.thain.server.entity.user.ThainUser;
import com.xiaomi.thain.server.service.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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

    public AdminController(@NonNull UserService userService) {
        this.userService = userService;
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
            if (userService.insertUser(userRequest)){
                return ApiResult.success();
            }
            return ApiResult.fail("userId has existed ");
        }
        return ApiResult.fail("Not Access");
    }
}
