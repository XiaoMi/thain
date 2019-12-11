/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.process.runtime.executor;

import com.google.common.collect.ImmutableList;
import com.mchange.lang.ThrowableUtils;
import com.xiaomi.thain.common.constant.FlowExecutionStatus;
import com.xiaomi.thain.common.constant.JobExecutionStatus;
import com.xiaomi.thain.common.exception.ThainCreateFlowExecutionException;
import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.exception.ThainFlowRunningException;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.common.model.JobExecutionModel;
import com.xiaomi.thain.common.model.dr.FlowDr;
import com.xiaomi.thain.common.model.dr.FlowExecutionDr;
import com.xiaomi.thain.common.model.dr.JobDr;
import com.xiaomi.thain.core.constant.FlowExecutionTriggerType;
import com.xiaomi.thain.core.process.ProcessEngine;
import com.xiaomi.thain.core.process.ProcessEngineStorage;
import com.xiaomi.thain.core.process.runtime.checker.JobConditionChecker;
import com.xiaomi.thain.core.process.runtime.executor.service.FlowExecutionService;
import com.xiaomi.thain.core.process.runtime.notice.FlowHttpNotice;
import com.xiaomi.thain.core.process.runtime.storage.FlowExecutionStorage;
import com.xiaomi.thain.core.thread.pool.ThainThreadPool;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.stream.Collectors.*;

/**
 * 任务执行器: 创建执行任务，管理执行流程
 *
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
public class FlowExecutor {

    private final long flowExecutionId;
    @NonNull
    private final FlowDr flowDr;
    @NonNull
    private final ProcessEngineStorage processEngineStorage;
    @NonNull
    private final JobConditionChecker jobConditionChecker;
    @NonNull
    private final FlowExecutionStorage flowExecutionStorage;
    @NonNull
    private final FlowExecutionService flowExecutionService;
    @NonNull
    private final FlowHttpNotice flowHttpNotice;
    @NonNull
    private final ThainThreadPool flowExecutionJobThreadPool;
    @NonNull
    private final Map<Long, JobExecutionModel> jobExecutionModelMap;

    /**
     * 当前未执行的节点
     */
    @NonNull
    private Collection<JobDr> notExecutedJobsPool;
    /**
     * 监控节点是否执行完
     */
    @NonNull
    private final Queue<CompletableFuture<Void>> jobFutureQueue;

    /**
     * 获取FlowExecutionExecutor实例
     * effect：为了获取flowExecutionId 会在数据库中创建一个flowExecution
     */
    private FlowExecutor(@NonNull FlowExecutionDr flowExecutionDr, @NonNull ProcessEngineStorage processEngineStorage)
            throws ThainException {
        this.processEngineStorage = processEngineStorage;
        this.flowDr = processEngineStorage.flowDao.getFlow(flowExecutionDr.flowId)
                .orElseThrow(ThainFlowRunningException::new);
        this.jobFutureQueue = new ConcurrentLinkedQueue<>();
        try {
            this.flowExecutionId = flowExecutionDr.id;
            val jobModelList = processEngineStorage.jobDao.getJobs(flowDr.getId());
            this.flowExecutionService = FlowExecutionService.getInstance(flowExecutionId, flowDr, processEngineStorage);
            this.notExecutedJobsPool = ImmutableList.copyOf(jobModelList);
            this.jobConditionChecker = JobConditionChecker.getInstance(flowExecutionId);
            this.flowExecutionStorage = FlowExecutionStorage.getInstance(flowExecutionId);
            this.flowHttpNotice = FlowHttpNotice.getInstance(flowDr.getCallbackUrl(), flowDr.getId(), flowExecutionId);
            this.flowExecutionJobThreadPool = processEngineStorage.flowExecutionJobThreadPool(flowExecutionId);
            this.jobExecutionModelMap = jobModelList.stream().collect(toMap(JobDr::getId, t -> {
                val jobExecutionModel = JobExecutionModel.builder()
                        .jobId(t.getId())
                        .flowExecutionId(flowExecutionId)
                        .status(JobExecutionStatus.NEVER.code)
                        .build();
                processEngineStorage.jobExecutionDao.add(jobExecutionModel);
                return jobExecutionModel;
            }));

        } catch (Exception e) {
            log.error("", e);
            throw new ThainCreateFlowExecutionException(flowDr.getId(), e.getMessage());
        }
    }

    /**
     * 开始执行流程，产生一个flowExecution，成功后异步执行start方法
     */
    public static void startProcess(@NonNull FlowExecutionDr flowExecutionDr,
                                    @NonNull ProcessEngineStorage processEngineStorage) throws ThainException {
        processEngineStorage.flowExecutionDao
                .updateFlowExecutionStatus(flowExecutionDr.id, FlowExecutionStatus.RUNNING.code);
        log.info("begin start flow: {}, flowExecutionId: {}, Trigger: {}",
                flowExecutionDr.flowId,
                flowExecutionDr.id,
                FlowExecutionTriggerType.getInstance(flowExecutionDr.triggerType));
        val flowExecutionService = new FlowExecutor(flowExecutionDr, processEngineStorage);
        flowExecutionService.start();
    }

    /**
     * 流程执行入口
     */
    private void start() {
        try {
            flowExecutionService.startFlowExecution();
            flowHttpNotice.sendStart();
            if (flowDr.getSlaDuration() > 0) {
                ProcessEngine.getInstance(processEngineStorage.processEngineId)
                        .thainFacade
                        .getSchedulerEngine()
                        .addSla(flowExecutionId, flowDr);
            }
            runExecutableJobs();
            while (!jobFutureQueue.isEmpty()) {
                jobFutureQueue.poll().join();
            }
        } catch (Exception e) {
            log.error("", e);
            flowExecutionService.addError(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                flowExecutionService.endFlowExecution();
                switch (flowExecutionService.getFlowEndStatus()) {
                    case SUCCESS:
                        flowHttpNotice.sendSuccess();
                        break;
                    case KILLED:
                        flowHttpNotice.sendKilled();
                        break;
                    case AUTO_KILLED:
                        flowHttpNotice.sendAutoKilled();
                        break;
                    case ERROR:
                    default:
                        flowHttpNotice.sendError(flowExecutionService.getErrorMessage());
                }
            } finally {
                FlowExecutionStorage.drop(flowExecutionId);
            }
        }
    }

    /**
     * 执行可以执行的节点
     */
    private synchronized void runExecutableJobs() {
        val flowExecutionModel = processEngineStorage.flowExecutionDao.getFlowExecution(flowExecutionId)
                .orElseThrow(() -> new ThainRuntimeException(
                        "Failed to read FlowExecution information, flowExecutionId: " + flowExecutionId));
        val flowExecutionStatus = FlowExecutionStatus.getInstance(flowExecutionModel.status);
        switch (flowExecutionStatus) {
            case KILLED:
                flowExecutionService.killed();
                return;
            case AUTO_KILLED:
                flowExecutionService.autoKilled();
                return;
            default:
        }
        val executableJobs = getExecutableJobs();
        executableJobs.forEach(job -> {
            val future = CompletableFuture.runAsync(() -> {
                flowExecutionService.addInfo("Start executing the job [" + job.getName() + "]");
                try {
                    JobExecutor.start(flowExecutionId, job, jobExecutionModelMap.get(job.getId()), processEngineStorage);
                } catch (Exception e) {
                    flowExecutionService.addError("Job[" + job.getName() + "] exception: "
                            + ExceptionUtils.getRootCauseMessage(e));
                    return;
                } catch (Throwable e) {
                    processEngineStorage.mailService.sendSeriousError(ThrowableUtils.extractStackTrace(e));
                    flowExecutionService.addError("Job[" + job.getName() + "] exception: " + e.getMessage());
                    return;
                }
                flowExecutionService.addInfo("Execute job[" + job.getName() + "] complete");
                flowExecutionStorage.addFinishJob(job.getName());
                runExecutableJobs();
            }, flowExecutionJobThreadPool);
            jobFutureQueue.add(future);
        });
    }

    private Collection<JobDr> getExecutableJobs() {
        val executableJobs = notExecutedJobsPool.stream()
                .filter(t -> jobConditionChecker.executable(t.getCondition())).collect(toSet());
        notExecutedJobsPool = notExecutedJobsPool.stream()
                .filter(t -> !executableJobs.contains(t)).collect(toList());
        return executableJobs;
    }

}
