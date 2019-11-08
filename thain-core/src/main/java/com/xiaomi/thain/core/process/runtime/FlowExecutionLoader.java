package com.xiaomi.thain.core.process.runtime;

import com.xiaomi.thain.common.constant.FlowExecutionStatus;
import com.xiaomi.thain.common.constant.FlowLastRunStatus;
import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException;
import com.xiaomi.thain.common.model.dr.FlowExecutionDr;
import com.xiaomi.thain.core.dao.FlowDao;
import com.xiaomi.thain.core.process.ProcessEngineStorage;
import com.xiaomi.thain.core.process.runtime.executor.FlowExecutor;
import com.xiaomi.thain.core.thread.pool.ThainThreadPool;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author liangyongrui
 */
@Log4j2
public class FlowExecutionLoader {

    public final Set<FlowExecutionDr> runningFlowExecution = new HashSet<>();
    @NonNull
    private final LinkedBlockingQueue<FlowExecutionDr> flowExecutionWaitingQueue;
    @NonNull
    private final ThainThreadPool flowExecutionThreadPool;
    @NonNull
    private final FlowDao flowDao;
    @NonNull
    private final ProcessEngineStorage processEngineStorage;
    @NonNull
    private final LinkedBlockingQueue<Boolean> idleThread;

    private FlowExecutionLoader(@NonNull ProcessEngineStorage processEngineStorage) throws InterruptedException {
        this.flowExecutionWaitingQueue = processEngineStorage.flowExecutionWaitingQueue;
        this.flowExecutionThreadPool = processEngineStorage.flowExecutionThreadPool;
        this.flowDao = processEngineStorage.flowDao;
        this.processEngineStorage = processEngineStorage;
        this.idleThread = new LinkedBlockingQueue<>();
        for (int i = 0; i < flowExecutionThreadPool.corePoolSize(); i++) {
            idleThread.put(true);
        }
        ThainThreadPool.DEFAULT_THREAD_POOL.execute(this::loopLoader);
    }

    public static FlowExecutionLoader getInstance(@NonNull ProcessEngineStorage processEngineStorage) throws InterruptedException {
        return new FlowExecutionLoader(processEngineStorage);
    }

    private void loopLoader() {
        while (true) {
            try {
                idleThread.take();
                val addFlowExecutionDp = flowExecutionWaitingQueue.take();
                checkFlowRunStatus(addFlowExecutionDp);
                CompletableFuture.runAsync(() -> runFlowExecution(addFlowExecutionDp), flowExecutionThreadPool)
                        .whenComplete((t, e) -> {
                            try {
                                idleThread.put(true);
                            } catch (Exception te) {
                                processEngineStorage.mailService.sendSeriousError(ExceptionUtils.getStackTrace(te));
                            }
                        });
            } catch (ThainRepeatExecutionException e) {
                log.warn(e.getMessage());
            } catch (Exception e) {
                log.error("", e);
                processEngineStorage.mailService.sendSeriousError(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private void checkFlowRunStatus(FlowExecutionDr flowExecutionDr) throws ThainException, ThainRepeatExecutionException {
        val flowModel = flowDao.getFlow(flowExecutionDr.flowId).orElseThrow(() -> new ThainException("flow does not exist"));
        val flowLastRunStatus = FlowLastRunStatus.getInstance(flowModel.lastRunStatus);
        if (flowLastRunStatus == FlowLastRunStatus.RUNNING) {
            processEngineStorage.flowExecutionDao.updateFlowExecutionStatus(flowExecutionDr.id, FlowExecutionStatus.DO_NOT_RUN_SAME_TIME.code);
            throw new ThainRepeatExecutionException("flow is running");
        }
    }

    private void runFlowExecution(@NonNull FlowExecutionDr flowExecutionDr) {
        try {
            runningFlowExecution.add(flowExecutionDr);
            FlowExecutor.startProcess(flowExecutionDr, processEngineStorage);
        } catch (Exception e) {
            log.error("runFlowExecution: ", e);
        } finally {
            runningFlowExecution.remove(flowExecutionDr);
        }
    }


}
