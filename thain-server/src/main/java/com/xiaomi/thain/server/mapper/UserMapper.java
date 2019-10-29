/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.mapper;

import com.xiaomi.thain.server.model.rq.UpdateUserRq;
import com.xiaomi.thain.server.model.ThainUser;
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
     * get userInfo by Id
     *
     * @param userId userId
     * @return see {@link ThainUser}
     */
    Optional<ThainUser> getUserById(@NonNull @Param("userId") String userId);

    /**
     * insert user
     *
     * @param user see {@link ThainUser}
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
     * delete user
     *
     * @param userId userId
     */
    @Delete("delete from thain_user where user_id=#{userId} ")
    void deleteUser(@NonNull String userId);

    /**
     * update user bySelective
     *
     * @param updateUserRq see {@link UpdateUserRq}
     */
    void updateUserBySelective(@NonNull UpdateUserRq updateUserRq);
}
