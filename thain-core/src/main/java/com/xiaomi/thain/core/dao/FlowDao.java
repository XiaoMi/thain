/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.dao;

import com.xiaomi.thain.common.constant.FlowLastRunStatus;
import com.xiaomi.thain.common.constant.FlowSchedulingStatus;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.common.model.rq.AddFlowRq;
import com.xiaomi.thain.common.model.rq.UpdateFlowRq;
import com.xiaomi.thain.core.mapper.FlowMapper;
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
import java.util.stream.Collectors;

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
@Log4j2
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FlowDao {
    @NonNull
    private final SqlSessionFactory sqlSessionFactory;
    @NonNull
    private final MailService mailService;

    public static FlowDao getInstance(@NonNull SqlSessionFactory sqlSessionFactory, @NonNull MailService mailService) {
        return new FlowDao(sqlSessionFactory, mailService);
    }

    /**
     * 自动释放sqlSession，事务执行mapper
     *
     * @param function function 是一个事务
     * @return 自定义的返回值
     */
    private <T> Optional<T> execute(@NonNull Function<FlowMapper, T> function) {
        try (val sqlSession = sqlSessionFactory.openSession()) {
            val apply = function.apply(sqlSession.getMapper(FlowMapper.class));
            sqlSession.commit();
            return Optional.ofNullable(apply);
        } catch (Exception e) {
            log.error("", e);
            mailService.sendSeriousError(ExceptionUtils.getStackTrace(e));
            return Optional.empty();
        }
    }

    /**
     * 数据库插入flow，成功flowModel插入id
     */
    public void addFlow(@NonNull AddFlowRq addFlowRq, @NonNull List<JobModel> jobModelList) {
        execute(t -> {
            t.addFlow(addFlowRq);
            if (addFlowRq.id == null) {
                return new ThainRuntimeException("add flow error");
            }
            return t.addJobList(jobModelList.stream().map(job -> job.toBuilder()
                    .flowId(addFlowRq.id).build()).collect(Collectors.toList()));
        });
    }

    /**
     * 更新flow
     */
    public void updateFlow(@NonNull UpdateFlowRq updateFlowRq, @NonNull List<JobModel> jobModelList) {
        execute(t -> {
            t.updateFlow(updateFlowRq);
            t.invalidJobList(updateFlowRq.id);
            if (jobModelList.isEmpty()) {
                return true;
            }
            return t.addJobList(jobModelList.stream()
                    .map(job -> job.toBuilder().flowId(updateFlowRq.id).build())
                    .collect(Collectors.toList()));
        });
    }

    /**
     * 删除flow
     */
    public void deleteFlow(long flowId) {
        execute(t -> {
            t.deleteFlow(flowId);
            return t.deleteJob(flowId);
        });
    }

    /**
     * 根据flow id 获取FlowModel
     */
    public Optional<com.xiaomi.thain.common.model.dr.FlowDr> getFlow(long flowId) {
        return execute(t -> t.getFlow(flowId));
    }

    /**
     * 修改最后一次运行状态
     *
     * @param flowId flow id
     * @param status 状态码
     */
    public void updateLastRunStatus(long flowId, @NonNull FlowLastRunStatus status) {
        execute(t -> t.updateLastRunStatus(flowId, status.code));
    }

    public void pauseFlow(long flowId) {
        execute(t -> t.updateSchedulingStatus(flowId, FlowSchedulingStatus.PAUSE.code));
    }

    public void killFlow(long flowId) {
        execute(t -> t.updateLastRunStatus(flowId, FlowLastRunStatus.KILLED.code));
    }

    public void updateSchedulingStatus(long flowId, @NonNull FlowSchedulingStatus scheduling) {
        execute(t -> t.updateSchedulingStatus(flowId, scheduling.code));
    }

    public List<Long> getAllFlowIds() {
        return execute(FlowMapper::getAllFlowIds).orElseGet(Collections::emptyList);
    }
}
