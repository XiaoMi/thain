/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.mapper;

import com.xiaomi.thain.server.entity.rq.UserRq;
import com.xiaomi.thain.server.entity.user.ThainUser;
import lombok.NonNull;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author miaoyu
 */
@Component
public interface UserMapper {
    /**
     * 根据id获取用户信息
     *
     * @param userId 用户id
     * @return see {@link ThainUser}
     */
    Optional<ThainUser> getUserById(@NonNull @Param("userId") String userId);

    /**
     * 添加用户
     *
     * @param user 用户对象
     */
    void insertUser(@NonNull ThainUser user);

    /**
     * select all Users
     *
     * @return see {@link ThainUser}
     */
    @Select("select id ,user_id ,user_name,email,admin from thain_user")
    List<ThainUser> getAllUsers();

    /**
     * @param  userId
     * delete user
     */
    @Delete("delete from thain_user where user_id=#{userId} ")
    void deleteUser(@NonNull String userId);

    /**
     * @param userRq
     * update user bySelective
     */
    void updateUserBySelective(@NonNull UserRq userRq);
}
