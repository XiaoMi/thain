/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.dao;

import com.xiaomi.thain.common.constant.FlowLastRunStatus;
import com.xiaomi.thain.common.constant.FlowSchedulingStatus;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.common.model.dp.UpdateFlowDp;
import com.xiaomi.thain.common.model.dr.FlowDr;
import com.xiaomi.thain.common.model.rq.kt.AddFlowRq;
import com.xiaomi.thain.common.model.rq.kt.AddJobRq;
import com.xiaomi.thain.core.mapper.FlowMapper;
import com.xiaomi.thain.core.model.dp.AddFlowDp;
import com.xiaomi.thain.core.model.dp.AddJobDp;
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
import java.util.stream.Collectors;

/**
 * Date 19-5-17 下午5:22
 *
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
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
    public Optional<Long> addFlow(@NonNull AddFlowRq addFlowRq,
                                  @NonNull List<AddJobRq> jobModelList,
                                  @NonNull FlowSchedulingStatus flowSchedulingStatus) {
        return execute(t -> {
            val addFlowDp = new AddFlowDp(addFlowRq, flowSchedulingStatus.code);
            t.addFlow(addFlowDp);
            if (addFlowDp.getId() == null) {
                throw new ThainRuntimeException("add flow error");
            }
            t.addJobList(jobModelList.stream()
                    .map(job -> AddJobDp.getInstance(job, addFlowDp.getId()))
                    .collect(Collectors.toList()));
            return addFlowDp.getId();
        });
    }

    /**
     * 更新flow
     */
    public void updateFlow(@NonNull UpdateFlowDp updateFlowDp, @NonNull List<AddJobRq> jobModelList) {
        execute(t -> {
            t.updateFlow(updateFlowDp);
            t.invalidJobList(updateFlowDp.getId());
            if (jobModelList.isEmpty()) {
                return true;
            }
            t.addJobList(jobModelList.stream()
                    .map(job -> AddJobDp.getInstance(job, updateFlowDp.getId()))
                    .collect(Collectors.toList()));
            return null;
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
    public Optional<FlowDr> getFlow(long flowId) {
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
