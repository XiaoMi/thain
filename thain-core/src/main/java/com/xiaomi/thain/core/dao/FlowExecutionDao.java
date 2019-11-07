/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.dao;

import com.xiaomi.thain.common.model.FlowExecutionModel;
import com.xiaomi.thain.common.model.dp.AddFlowExecutionDp;
import com.xiaomi.thain.core.mapper.FlowExecutionMapper;
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
public class FlowExecutionDao {
    @NonNull
    private final SqlSessionFactory sqlSessionFactory;
    @NonNull
    private final MailService mailService;
    private final int dataReserveDays;

    public static FlowExecutionDao getInstance(@NonNull SqlSessionFactory sqlSessionFactory, @NonNull MailService mailService,
                                               int dataReserveDays) {
        return new FlowExecutionDao(sqlSessionFactory, mailService, dataReserveDays);
    }

    /**
     * 自动释放sqlSession，事务执行mapper
     *
     * @param function function 是一个事务
     * @return 自定义的返回值
     */
    private <T> Optional<T> execute(@NonNull Function<FlowExecutionMapper, T> function) {
        try (val sqlSession = sqlSessionFactory.openSession()) {
            val apply = function.apply(sqlSession.getMapper(FlowExecutionMapper.class));
            sqlSession.commit();
            return Optional.ofNullable(apply);
        } catch (Exception e) {
            log.error("", e);
            mailService.sendSeriousError(ExceptionUtils.getStackTrace(e));
            return Optional.empty();
        }
    }

    /**
     * 数据库插入flowExecution
     */
    public void addFlowExecution_(@NonNull FlowExecutionModel flowExecutionModel) {
        execute(t -> t.addFlowExecution_(flowExecutionModel));
    }

    /**
     * 数据库插入flowExecution
     */
    public void addFlowExecution(@NonNull AddFlowExecutionDp addFlowExecutionDp) {
        execute(t -> t.addFlowExecution(addFlowExecutionDp));
    }


    /**
     * 更新flowExecution日志
     */
    public void updateLogs(long flowExecutionId, @NonNull String content) {
        execute(t -> t.updateLogs(flowExecutionId, content));
    }

    public void updateFlowExecutionStatus(long flowExecutionId, int status) {
        execute(t -> t.updateFlowExecutionStatus(flowExecutionId, status));
    }

    /**
     * 清理失效日志
     */
    public void cleanFlowExecution() {
        execute(t -> t.clearFlowExecution(dataReserveDays));
    }

    public Optional<FlowExecutionModel> getFlowExecution(long flowExecutionId) {
        return execute(t -> t.getFlowExecution(flowExecutionId));
    }

    public Optional<List<FlowExecutionModel>> getLatest(long flowId, long numbers) {
        return execute(t -> t.getLatest(flowId, numbers));
    }

    public void killFlowExecution(long flowExecutionId) {
        execute(t -> t.killFlowExecution(flowExecutionId));
    }

    public List<Long> getNeedDeleteFlowExecutionId(@NonNull List<Long> flowIds) {
        if (flowIds.isEmpty()) {
            return Collections.emptyList();
        }
        return execute(t -> t.getNeedDeleteFlowExecutionId(flowIds)).orElseGet(Collections::emptyList);
    }

    public void deleteFlowExecutionByIds(@NonNull List<Long> needDeleteFlowExecutionIds) {
        if (needDeleteFlowExecutionIds.isEmpty()) {
            return;
        }
        execute(t -> {
            t.deleteFlowExecutionByIds(needDeleteFlowExecutionIds);
            return null;
        });
    }

    public List<Long> getAllFlowExecutionIds() {
        return execute(FlowExecutionMapper::getAllFlowExecutionIds).orElseGet(Collections::emptyList);
    }
}
