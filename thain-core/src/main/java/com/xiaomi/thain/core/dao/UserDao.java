/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.dao;

import com.xiaomi.thain.core.entity.ThainUser;
import com.xiaomi.thain.core.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
@Log4j2
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {
    @NonNull
    private final SqlSessionFactory sqlSessionFactory;

    public static UserDao getInstance(@NonNull SqlSessionFactory sqlSessionFactory) {
        return new UserDao(sqlSessionFactory);
    }

    /**
     * 自动释放sqlSession，事务执行mapper
     *
     * @param function function 是一个事务
     * @return 自定义的返回值
     */
    private <T> Optional<T> execute(@NonNull Function<UserMapper, T> function) {
        try (val sqlSession = sqlSessionFactory.openSession()) {
            val apply = function.apply(sqlSession.getMapper(UserMapper.class));
            sqlSession.commit();
            return Optional.ofNullable(apply);
        } catch (Exception e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    public List<ThainUser> getAdminUsers() {
        return execute(UserMapper::getAdminUsers).orElseGet(Collections::emptyList);
    }

}
