/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process.runtime.executor.service;

import com.xiaomi.thain.common.constant.FlowExecutionStatus;
import com.xiaomi.thain.common.constant.FlowLastRunStatus;
import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.model.dr.FlowDr;
import com.xiaomi.thain.core.dao.FlowExecutionDao;
import com.xiaomi.thain.core.process.ProcessEngine;
import com.xiaomi.thain.core.process.ProcessEngineStorage;
import com.xiaomi.thain.core.process.runtime.log.FlowExecutionLogHandler;
import com.xiaomi.thain.core.process.runtime.notice.MailNotice;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Date 19-5-21 上午10:46
 * 任务服务类，对不影响任务执行的方法进行管理，如：日志,状态等
 *
 * @author liangyongrui@xiaomi.com
 */
@Log4j2
public class FlowExecutionService {

    public final long flowExecutionId;

    @NonNull
    private final FlowExecutionLogHandler flowExecutionLogHandler;
    @NonNull
    private final MailNotice mailNotice;
    @NonNull
    private final FlowService flowService;
    @NonNull
    private final FlowExecutionDao flowExecutionDao;
    @NonNull
    private final ProcessEngineStorage processEngineStorage;
    @NonNull
    private final FlowDr flowDr;

    /**
     * 如果是异常结束,异常信息.
     * 正常结束时，errorMessage为""
     */
    @Getter
    @NonNull
    private String errorMessage = "";

    /**
     * 流程结束状态
     */
    @Getter
    @NonNull
    private FlowLastRunStatus flowEndStatus = FlowLastRunStatus.SUCCESS;

    @NonNull
    private FlowExecutionStatus flowExecutionEndStatus = FlowExecutionStatus.SUCCESS;

    private static final Map<Long, FlowExecutionService> FLOW_EXECUTION_SERVICE_MAP = new ConcurrentHashMap<>();

    private FlowExecutionService(long flowExecutionId,
                                 @NonNull FlowDr flowDr,
                                 @NonNull ProcessEngineStorage processEngineStorage) {
        this.processEngineStorage = processEngineStorage;
        this.flowExecutionId = flowExecutionId;
        this.flowService = FlowService.getInstance(flowDr.id, processEngineStorage);
        this.flowExecutionLogHandler = FlowExecutionLogHandler.getInstance(flowExecutionId, processEngineStorage);
        this.flowExecutionDao = processEngineStorage.flowExecutionDao;
        this.mailNotice = processEngineStorage.getMailNotice(flowDr.callbackEmail);
        this.flowDr = flowDr;
    }

    public static FlowExecutionService getInstance(long flowExecutionId,
                                                   @NonNull FlowDr flowModel,
                                                   @NonNull ProcessEngineStorage processEngineStorage) {
        return FLOW_EXECUTION_SERVICE_MAP.computeIfAbsent(flowExecutionId,
                id -> new FlowExecutionService(id, flowModel, processEngineStorage));
    }

    /**
     * 开始任务
     */
    public void startFlowExecution() {
        try {
            flowService.startFlow();
            flowExecutionLogHandler.addInfo("begin to execute flow：" + flowExecutionId);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 添加错误
     */
    public void addError(@NonNull String message) {
        this.errorMessage = message;
        flowEndStatus = FlowLastRunStatus.ERROR;
        flowExecutionEndStatus = FlowExecutionStatus.ERROR;
    }

    /**
     * 添加错误
     */
    public void killed() {
        this.errorMessage = "manual kill";
        flowEndStatus = FlowLastRunStatus.KILLED;
        flowExecutionEndStatus = FlowExecutionStatus.KILLED;
    }

    /**
     * 结束任务
     */
    public void endFlowExecution() {
        try {
            switch (flowEndStatus) {
                case SUCCESS:
                    flowExecutionLogHandler.endSuccess();
                    break;
                case ERROR:
                default:
                    flowExecutionLogHandler.endError(errorMessage);
                    mailNotice.sendError(errorMessage);
                    checkContinuousFailure();

            }
            processEngineStorage.flowExecutionDao.updateFlowExecutionStatus(flowExecutionId, flowExecutionEndStatus.code);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            flowService.endFlow(flowEndStatus);
            close();
        }
    }

    public void addInfo(@NonNull String s) {
        flowExecutionLogHandler.addInfo(s);
    }

    /**
     * 连续失败暂停任务
     */
    private void checkContinuousFailure() throws ThainException, IOException, MessagingException {
        if (flowDr.pauseContinuousFailure > 0) {
            val latest = flowExecutionDao.getLatest(flowDr.id, flowDr.pauseContinuousFailure).orElseGet(Collections::emptyList);
            val count = latest.stream().filter(t -> FlowExecutionStatus.getInstance(t.status) == FlowExecutionStatus.ERROR).count();
            if (count >= flowDr.pauseContinuousFailure - 1) {
                ProcessEngine.getInstance(processEngineStorage.processEngineId).thainFacade.pauseFlow(flowDr.id);
                if (StringUtils.isNotBlank(flowDr.emailContinuousFailure)) {
                    processEngineStorage.mailService.send(
                            flowDr.emailContinuousFailure.trim().split(","),
                            "Thain 任务连续失败通知",
                            "您的任务：" + flowDr.name + ", 连续失败了" + flowDr.pauseContinuousFailure + "次，任务已经暂停。最近一次失败原因：" + errorMessage
                    );
                }
            }
        }
    }

    private void close() {
        FLOW_EXECUTION_SERVICE_MAP.remove(flowExecutionId);
    }
}
