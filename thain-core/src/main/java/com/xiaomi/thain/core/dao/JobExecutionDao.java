/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.dao;

import com.xiaomi.thain.common.constant.JobExecutionStatus;
import com.xiaomi.thain.common.model.JobExecutionModel;
import com.xiaomi.thain.core.mapper.JobExecutionMapper;
import com.xiaomi.thain.core.process.service.MailService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
public class JobExecutionDao {
    private final SqlSessionFactory sqlSessionFactory;
    private final MailService mailService;

    public static JobExecutionDao getInstance(@NonNull SqlSessionFactory sqlSessionFactory, @NonNull MailService mailService) {
        return new JobExecutionDao(sqlSessionFactory, mailService);
    }

    /**
     * 自动释放sqlSession，事务执行mapper
     *
     * @param function function 是一个事务
     * @return 自定义的返回值
     */
    private <T> Optional<T> execute(@NonNull Function<JobExecutionMapper, T> function) {
        try (val sqlSession = sqlSessionFactory.openSession()) {
            val apply = function.apply(sqlSession.getMapper(JobExecutionMapper.class));
            sqlSession.commit();
            return Optional.ofNullable(apply);
        } catch (Exception e) {
            log.error("", e);
            mailService.sendSeriousError(ExceptionUtils.getStackTrace(e));
            return Optional.empty();
        }
    }

    /**
     * 数据库插入JobExecutionModel
     * jobExecutionModel 会被插入自增id
     */
    public void add(@NonNull JobExecutionModel jobExecutionModel) {
        execute(t -> t.add(jobExecutionModel));
    }

    public void updateLogs(long jobExecutionId, @NonNull String logs) {
        execute(t -> t.updateLogs(jobExecutionId, logs));
    }

    public void updateStatus(long jobExecutionId, @NonNull JobExecutionStatus status) {
        execute(t -> t.updateStatus(jobExecutionId, status.code));
    }

    public void updateCreateTimeAndStatus(long jobExecutionId, @NonNull JobExecutionStatus status) {
        execute(t -> {
            t.updateCreateTime(jobExecutionId);
            return t.updateStatus(jobExecutionId, status.code);
        });
    }

    public List<Long> getNeedDeleteJobExecutionIds(@NonNull List<Long> flowExecutionIds) {
        if (flowExecutionIds.isEmpty()) {
            return Collections.emptyList();
        }
        return execute(t -> t.getNeedDeleteJobExecutionIds(flowExecutionIds)).orElseGet(Collections::emptyList);
    }

    public void deleteJobExecutionByIds(@NonNull List<Long> needDeleteJobExecutionIds) {
        if (needDeleteJobExecutionIds.isEmpty()) {
            return;
        }
        execute(t -> t.deleteJobExecutionByIds(needDeleteJobExecutionIds));
    }

    public void killJobExecution(long flowExecutionId) {
        execute(t -> t.killJobExecution(flowExecutionId));
    }

    public void deleteJobExecutionByFlowExecutionIds(@NonNull List<Long> flowExecutionIds) {
        if (flowExecutionIds.isEmpty()) {
            return;
        }
        execute(t -> t.deleteJobExecutionByFlowExecutionIds(flowExecutionIds));
    }
}
