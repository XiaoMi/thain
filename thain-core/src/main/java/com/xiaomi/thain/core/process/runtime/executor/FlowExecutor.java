/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.process.runtime.executor;

import com.google.common.collect.ImmutableList;
import com.xiaomi.thain.common.constant.FlowExecutionStatus;
import com.xiaomi.thain.common.constant.FlowLastRunStatus;
import com.xiaomi.thain.common.constant.JobExecutionStatus;
import com.xiaomi.thain.common.exception.ThainCreateFlowExecutionException;
import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.exception.ThainFlowRunningException;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.common.model.FlowExecutionModel;
import com.xiaomi.thain.common.model.FlowModel;
import com.xiaomi.thain.common.model.JobExecutionModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.core.constant.FlowExecutionTriggerType;
import com.xiaomi.thain.core.process.ProcessEngine;
import com.xiaomi.thain.core.process.ProcessEngineStorage;
import com.xiaomi.thain.core.process.runtime.checker.JobConditionChecker;
import com.xiaomi.thain.core.process.runtime.executor.service.FlowExecutionService;
import com.xiaomi.thain.core.process.runtime.notice.HttpNotice;
import com.xiaomi.thain.core.process.runtime.storage.FlowExecutionStorage;
import com.xiaomi.thain.core.thread.pool.ThainThreadPool;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.stream.Collectors.*;

/**
 * 任务执行器: 创建执行任务，管理执行流程
 *
 * @author liangyongrui@xiaomi.com
 */
@Log4j2
public class FlowExecutor {

    private final long flowExecutionId;
    @NonNull
    private final FlowModel flowModel;
    @NonNull
    private final ProcessEngineStorage processEngineStorage;
    @NonNull
    private final JobConditionChecker jobConditionChecker;
    @NonNull
    private final FlowExecutionStorage flowExecutionStorage;
    @NonNull
    private final FlowExecutionService flowExecutionService;
    @NonNull
    private final HttpNotice httpNotice;
    @NonNull
    private final ThainThreadPool flowExecutionJobThreadPool;
    @NonNull
    private final Map<Long, JobExecutionModel> jobExecutionModelMap;

    /**
     * 当前未执行的节点
     */
    @NonNull
    private Collection<JobModel> notExecutedJobsPool;
    /**
     * 监控节点是否执行完，也可用于中断任务 或 中断节点
     */
    @NonNull
    private final Map<String, CompletableFuture<Void>> jobFutureMap;
    /**
     * 监控节点是否执行完
     */
    @NonNull
    private final Queue<CompletableFuture<Void>> jobFutureQueue;

    /**
     * 是否killed
     */
    private long newFlowExecutor(long flowId, @NonNull FlowExecutionTriggerType flowExecutionTriggerType) throws ThainException {
        try {
            int triggerTypeCode;
            switch (flowExecutionTriggerType) {
                case MANUAL:
                    triggerTypeCode = 1;
                    break;
                case AUTOMATIC:
                    triggerTypeCode = 2;
                    break;
                default:
                    throw new ThainRuntimeException("The trigger type is unknown");
            }

            val flowExecutionModel = FlowExecutionModel.builder()
                    .flowId(flowId)
                    .hostInfo(InetAddress.getLocalHost().toString())
                    .status(FlowExecutionStatus.RUNNING.code)
                    .triggerType(triggerTypeCode).build();

            //创建任务失败
            processEngineStorage.flowExecutionDao.addFlowExecution_(flowExecutionModel);
            if (flowExecutionModel.id == 0) {
                throw new ThainException("Failed to insert into database");
            }
            return flowExecutionModel.id;
        } catch (Exception e) {
            try {
                processEngineStorage.mailService.sendSeriousError(
                        "FlowExecution create failed, flowId:" + flowId + "detail message：" + ExceptionUtils.getStackTrace(e));
            } catch (Exception ex) {
                throw new ThainCreateFlowExecutionException(e);
            }
            throw new ThainCreateFlowExecutionException(e);
        }
    }

    /**
     * 获取FlowExecutionExecutor实例
     * effect：为了获取flowExecutionId 会在数据库中创建一个flowExecution
     */
    private FlowExecutor(@NonNull FlowModel flowModel, @NonNull ProcessEngineStorage processEngineStorage,
                         @NonNull FlowExecutionTriggerType flowExecutionTriggerType)
            throws ThainException {
        this.processEngineStorage = processEngineStorage;
        this.flowModel = flowModel;
        this.jobFutureMap = new ConcurrentHashMap<>();
        this.jobFutureQueue = new ConcurrentLinkedQueue<>();
        try {
            this.flowExecutionId = newFlowExecutor(flowModel.id, flowExecutionTriggerType);
            val jobModelList = processEngineStorage.jobDao.getJobs(flowModel.id).orElseThrow(ThainCreateFlowExecutionException::new);
            this.flowExecutionService = FlowExecutionService.getInstance(flowExecutionId, flowModel, processEngineStorage);
            this.notExecutedJobsPool = ImmutableList.copyOf(jobModelList);
            this.jobConditionChecker = JobConditionChecker.getInstance(flowExecutionId);
            this.flowExecutionStorage = FlowExecutionStorage.getInstance(flowExecutionId);
            this.httpNotice = HttpNotice.getInstance(flowModel.callbackUrl, flowModel.id, flowExecutionId);
            this.flowExecutionJobThreadPool = processEngineStorage.flowExecutionJobThreadPool(flowExecutionId);
            this.jobExecutionModelMap = jobModelList.stream().collect(toMap(t -> t.id, t -> {
                val jobExecutionModel = JobExecutionModel.builder()
                        .jobId(t.id)
                        .flowExecutionId(flowExecutionId)
                        .status(JobExecutionStatus.NEVER.code)
                        .build();
                processEngineStorage.jobExecutionDao.add(jobExecutionModel);
                return jobExecutionModel;
            }));
        } catch (Exception e) {
            log.error("", e);
            throw new ThainCreateFlowExecutionException(flowModel.id, e.getMessage());
        }
    }

    /**
     * 开始执行流程，产生一个flowExecution，成功后异步执行start方法
     */
    public static void startProcess(long flowId,
                                    @NonNull ProcessEngineStorage processEngineStorage,
                                    @NonNull FlowExecutionTriggerType flowExecutionTriggerType) throws ThainException {
        val flowModel = processEngineStorage.flowDao.getFlow(flowId).orElseThrow(() -> new ThainException("flow does not exist"));
        val flowLastRunStatus = FlowLastRunStatus.getInstance(flowModel.lastRunStatus);
        if (flowLastRunStatus == FlowLastRunStatus.RUNNING) {
            throw new ThainFlowRunningException(flowId);
        }
        val flowExecutionService = new FlowExecutor(flowModel, processEngineStorage, flowExecutionTriggerType);
        CompletableFuture.runAsync(flowExecutionService::start, processEngineStorage.flowExecutionThreadPool);
    }

    /**
     * 流程执行入口
     */
    private void start() {
        try {
            flowExecutionService.startFlowExecution();
            httpNotice.sendStart();
            if (flowModel.slaDuration > 0) {
                ProcessEngine.getInstance(processEngineStorage.processEngineId)
                        .thainFacade
                        .schedulerEngine
                        .addSla(flowExecutionId, flowModel);
            }
            runExecutableJobs();
            while (!jobFutureQueue.isEmpty()) {
                jobFutureQueue.poll().join();
            }
        } catch (Exception e) {
            log.error("", e);
            flowExecutionService.addError(ExceptionUtils.getStackTrace(e));
        } finally {
            flowExecutionService.endFlowExecution();
            switch (flowExecutionService.getFlowEndStatus()) {
                case SUCCESS:
                    httpNotice.sendSuccess();
                    break;
                default:
                    httpNotice.sendError(flowExecutionService.getErrorMessage());
            }
            FlowExecutionStorage.drop(flowExecutionId);
        }
    }

    /**
     * 执行可以执行的节点
     */
    private synchronized void runExecutableJobs() {
        val flowExecutionModel = processEngineStorage.flowExecutionDao.getFlowExecution(flowExecutionId).orElseThrow(
                () -> new ThainRuntimeException("Failed to read FlowExecution information, flowExecutionId: " + flowExecutionId));
        val flowExecutionStatus = FlowExecutionStatus.getInstance(flowExecutionModel.status);
        if (flowExecutionStatus == FlowExecutionStatus.KILLED) {
            flowExecutionService.killed();
            return;
        }
        val executableJobs = getExecutableJobs();
        executableJobs.forEach(job -> {
            val future = CompletableFuture.runAsync(() -> {
                flowExecutionService.addInfo("Start executing the job [" + job.name + "]");
                if (JobExecutor.start(flowExecutionId, job, jobExecutionModelMap.get(job.id), processEngineStorage)) {
                    flowExecutionService.addInfo("Execute job[" + job.name + "] complete");
                    flowExecutionStorage.addFinishJob(job.name);
                } else {
                    flowExecutionService.addError("Job[" + job.name + "] exception");
                }
                runExecutableJobs();
            }, flowExecutionJobThreadPool);
            jobFutureMap.put(job.name, future);
            jobFutureQueue.add(future);
        });
    }

    private Collection<JobModel> getExecutableJobs() {
        val executableJobs = notExecutedJobsPool.stream()
                .filter(t -> jobConditionChecker.executable(t.condition)).collect(toSet());
        notExecutedJobsPool = notExecutedJobsPool.stream()
                .filter(t -> !executableJobs.contains(t)).collect(toList());
        return executableJobs;
    }

}
