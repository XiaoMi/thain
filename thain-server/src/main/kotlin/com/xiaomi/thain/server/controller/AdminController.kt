package com.xiaomi.thain.server.controller

import com.alibaba.fastjson.JSON
import com.github.pagehelper.PageInfo
import com.github.pagehelper.page.PageMethod
import com.xiaomi.thain.common.entity.ApiResult
import com.xiaomi.thain.server.handler.ThreadLocalUser
import com.xiaomi.thain.server.model.ThainUser
import com.xiaomi.thain.server.model.rp.UserRp
import com.xiaomi.thain.server.model.rp.X5ConfigRp
import com.xiaomi.thain.server.model.rq.AddUserRq
import com.xiaomi.thain.server.model.rq.UpdateUserRq
import com.xiaomi.thain.server.model.rq.X5ConfigRq
import com.xiaomi.thain.server.service.UserService
import com.xiaomi.thain.server.service.X5Service
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/admin")
class AdminController(private val userService: UserService, private val x5Service: X5Service) {
    @GetMapping("/users")
    fun getAllUserInfo(@RequestParam(defaultValue = "1") page: Int, @RequestParam(defaultValue = "10") pageSize: Int): ApiResult {
        if (ThreadLocalUser.isAdmin) {
            PageMethod.startPage<Any>(page, pageSize)
            val thainUserPageInfo = PageInfo(userService.allUsers)
            return ApiResult.success(thainUserPageInfo.list.map { t: ThainUser ->
                UserRp(userId = t.userId,
                        username = t.username,
                        admin = t.admin,
                        email = t.email)
            }, thainUserPageInfo.total,
                    thainUserPageInfo.pageNum,
                    thainUserPageInfo.pageSize)
        }
        return ApiResult.fail("Not allow to access")
    }

    @DeleteMapping("/user/{userId}")
    fun deleteUser(@PathVariable("userId") userId: String): ApiResult {
        if (ThreadLocalUser.isAdmin) {
            userService.deleteUser(userId)
            return ApiResult.success()
        }
        return ApiResult.fail("Not Access ")
    }

    @PostMapping("/user")
    fun addUser(@RequestBody addUserRq: AddUserRq): ApiResult {
        return if (ThreadLocalUser.isAdmin) {
            if (userService.insertUser(addUserRq)) {
                ApiResult.success()
            } else ApiResult.fail("userId has existed ")
        } else ApiResult.fail("Not Access to delete user")
    }

    @PatchMapping("/user")
    fun updateUser(@RequestBody request: UpdateUserRq): ApiResult {
        return if (ThreadLocalUser.isAdmin) {
            if (userService.updateUser(request)) {
                ApiResult.success()
            } else ApiResult.fail("UserId is not existed")
        } else ApiResult.fail("Not Access to update user")
    }

    @GetMapping("/clients")
    fun getAllConfigs(@RequestParam(defaultValue = "1") page: Int, @RequestParam(defaultValue = "10") pageSize: Int): ApiResult {
        if (ThreadLocalUser.isAdmin) {
            PageMethod.startPage<Any>(page, pageSize)
            val pageInfo = PageInfo(x5Service.allConfigs)
            val x5Configs = mutableListOf<X5ConfigRp>()
            pageInfo.list.forEach {
                val principals = JSON.parseArray(it.principal, String::class.java)
                x5Configs.add(X5ConfigRp(
                        appId = it.appId,
                        appKey = it.appKey,
                        appName = it.appName,
                        description = it.appDescription,
                        createTime = it.createTime.time,
                        principals = principals))
            }
            return ApiResult.success(x5Configs, pageInfo.total, pageInfo.pageNum, pageInfo.pageSize)
        }
        return ApiResult.fail("Not Access to get clients")
    }

    @DeleteMapping("/client/{appId}")
    fun deleteConfig(@PathVariable("appId") appId: String): ApiResult {
        if (ThreadLocalUser.isAdmin) {
            x5Service.deleteX5Config(appId)
            return ApiResult.success()
        }
        return ApiResult.fail("Not Access to delete x5config")
    }

    @PostMapping("/client")
    fun addConfig(@RequestBody x5ConfigRq: X5ConfigRq): ApiResult {
        return if (ThreadLocalUser.isAdmin) {
            if (x5Service.insertX5Config(x5ConfigRq)) {
                ApiResult.success()
            } else ApiResult.fail("AppId existed or principal is null ")
        } else ApiResult.fail("Not Allow to Access")
    }

    @PatchMapping("/client")
    fun updateConfig(@RequestBody x5ConfigRq: X5ConfigRq): ApiResult {
        return if (ThreadLocalUser.isAdmin) {
            if (x5Service.updateX5Config(x5ConfigRq)) {
                ApiResult.success()
            } else ApiResult.fail("X5config has been delete")
        } else ApiResult.fail("Not Allow to update")
    }

}
