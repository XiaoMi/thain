package com.xiaomi.thain.core.process.runtime;

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

    private FlowExecutionLoader(@NonNull ProcessEngineStorage processEngineStorage) {
        this.flowExecutionWaitingQueue = processEngineStorage.flowExecutionWaitingQueue;
        this.flowExecutionThreadPool = processEngineStorage.flowExecutionThreadPool;
        this.flowDao = processEngineStorage.flowDao;
        this.processEngineStorage = processEngineStorage;
        ThainThreadPool.DEFAULT_THREAD_POOL.execute(this::loopLoader);
    }

    public static FlowExecutionLoader getInstance(@NonNull ProcessEngineStorage processEngineStorage) {
        return new FlowExecutionLoader(processEngineStorage);
    }

    private void loopLoader() {
        while (true) {
            try {
                //todo 执行队列是否满, 如果没满的话：
                val addFlowExecutionDp = flowExecutionWaitingQueue.take();
                checkFlowRunStatus(addFlowExecutionDp.flowId);
                flowExecutionThreadPool.execute(() -> runFlowExecution(addFlowExecutionDp));
            } catch (ThainRepeatExecutionException e) {
                log.warn(e.getMessage());
            } catch (Exception e) {
                log.error("", e);
                processEngineStorage.mailService.sendSeriousError(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private void checkFlowRunStatus(long flowId) throws ThainException, ThainRepeatExecutionException {
        val flowModel = flowDao.getFlow(flowId).orElseThrow(() -> new ThainException("flow does not exist"));
        val flowLastRunStatus = FlowLastRunStatus.getInstance(flowModel.lastRunStatus);
        if (flowLastRunStatus == FlowLastRunStatus.RUNNING) {
            throw new ThainRepeatExecutionException("flow is running");
        }
    }

    private void runFlowExecution(@NonNull FlowExecutionDr flowExecutionDr) {
        try {
            runningFlowExecution.add(flowExecutionDr);
            FlowExecutor.startProcess(flowExecutionDr, processEngineStorage);
            runningFlowExecution.remove(flowExecutionDr);
        } catch (ThainException e) {
            log.error("runFlowExecution: ", e);
        }
    }


}
