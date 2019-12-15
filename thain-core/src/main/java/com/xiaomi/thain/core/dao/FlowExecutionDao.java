/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.dao;

import com.xiaomi.thain.common.model.FlowExecutionModel;
import com.xiaomi.thain.common.model.dp.AddFlowExecutionDp;
import com.xiaomi.thain.common.model.dr.FlowExecutionDr;
import com.xiaomi.thain.core.mapper.FlowExecutionMapper;
import com.xiaomi.thain.core.process.service.MailService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FlowExecutionDao {
    @NonNull
    private final SqlSessionFactory sqlSessionFactory;
    @NonNull
    private final MailService mailService;

    public static FlowExecutionDao getInstance(@NonNull SqlSessionFactory sqlSessionFactory, @NonNull MailService mailService) {
        return new FlowExecutionDao(sqlSessionFactory, mailService);
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
     * clean up expired flow execution
     */
    public void cleanUpExpiredFlowExecution() {
        execute(FlowExecutionMapper::cleanUpExpiredFlowExecution);
    }

    public Optional<FlowExecutionDr> getFlowExecution(long flowExecutionId) {
        return execute(t -> t.getFlowExecution(flowExecutionId));
    }

    public List<FlowExecutionModel> getLatest(long flowId, long numbers) {
        return execute(t -> t.getLatest(flowId, numbers)).orElse(Collections.emptyList());
    }

    public void setFlowExecutionHeartbeat(@NonNull List<Long> flowExecutionIds) {
        if (flowExecutionIds.isEmpty()) {
            return;
        }
        execute(t -> t.setFlowExecutionHeartbeat(flowExecutionIds));
    }

    /**
     * 获取超过1min没心跳的任务
     */
    public List<FlowExecutionDr> getDead() {
        return execute(FlowExecutionMapper::getDead).orElseGet(Collections::emptyList);
    }

    /**
     * 重新排队
     */
    public void reWaiting(@NonNull List<Long> flowExecutionIds) {
        execute(t -> t.reWaiting(flowExecutionIds));
    }

    public void updateHostInfo(long id, @NonNull String hostInfo) {
        execute(t -> t.updateHostInfo(id, hostInfo));
    }
}
