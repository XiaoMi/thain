/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.dao;

import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.core.mapper.JobMapper;
import com.xiaomi.thain.core.process.service.MailService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JobDao {
    @NonNull
    private final SqlSessionFactory sqlSessionFactory;
    @NonNull
    private final MailService mailService;

    public static JobDao getInstance(@NonNull SqlSessionFactory sqlSessionFactory, @NonNull MailService mailService) {
        return new JobDao(sqlSessionFactory, mailService);
    }

    /**
     * 自动释放sqlSession，事务执行mapper
     *
     * @param function function 是一个事务
     * @return 自定义的返回值
     */
    private <T> Optional<T> execute(@NonNull Function<JobMapper, T> function) {
        try (val sqlSession = sqlSessionFactory.openSession()) {
            val apply = function.apply(sqlSession.getMapper(JobMapper.class));
            sqlSession.commit();
            return Optional.ofNullable(apply);
        } catch (Exception e) {
            log.error("", e);
            mailService.sendSeriousError(ExceptionUtils.getStackTrace(e));
            return Optional.empty();
        }
    }

    public Optional<List<JobModel>> getJobs(long flowId) {
        return execute(t -> t.getJobs(flowId));
    }
}
