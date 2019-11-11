/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core;

import com.xiaomi.thain.common.constant.FlowExecutionStatus;
import com.xiaomi.thain.common.constant.FlowSchedulingStatus;
import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.exception.ThainMissRequiredArgumentsException;
import com.xiaomi.thain.common.exception.ThainRepeatExecutionException;
import com.xiaomi.thain.common.exception.scheduler.ThainSchedulerException;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.common.model.rq.AddRq;
import com.xiaomi.thain.common.model.rq.UpdateFlowRq;
import com.xiaomi.thain.core.process.ProcessEngine;
import com.xiaomi.thain.core.process.ProcessEngineConfiguration;
import com.xiaomi.thain.core.scheduler.SchedulerEngine;
import com.xiaomi.thain.core.scheduler.SchedulerEngineConfiguration;
import com.xiaomi.thain.core.utils.SendModifyUtils;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-16 下午8:38
 */
@Log4j2
public class ThainFacade {

    private static final String NON_EXIST_FLOW = "flow does not exist:{0}";
    @NonNull
    public final SchedulerEngine schedulerEngine;
    @NonNull
    private final ProcessEngine processEngine;

    private ThainFacade(@NonNull ProcessEngineConfiguration processEngineConfiguration,
                        @NonNull SchedulerEngineConfiguration schedulerEngineConfiguration)
            throws ThainSchedulerException, ThainMissRequiredArgumentsException, IOException, SQLException, InterruptedException {
        processEngine = ProcessEngine.newInstance(processEngineConfiguration, this);
        schedulerEngine = SchedulerEngine.getInstance(schedulerEngineConfiguration, processEngine);
        schedulerEngine.start();
    }

    public static ThainFacade getInstance(@NonNull ProcessEngineConfiguration processEngineConfiguration,
                                          @NonNull SchedulerEngineConfiguration schedulerEngineConfiguration)
            throws ThainSchedulerException, ThainMissRequiredArgumentsException, IOException, SQLException, InterruptedException {
        return new ThainFacade(processEngineConfiguration, schedulerEngineConfiguration);
    }

    /**
     * 新建任务, cron为空的则只部署，不调度, 这个flowJson是不含id的，如果含id也没用
     */
    public long addFlow(@NonNull AddRq addRq) throws ThainException {
        val flowId = processEngine.addFlow(addRq.flowModel, addRq.jobModelList).orElseThrow(() -> new ThainException("failed to insert flow"));
        if (StringUtils.isBlank(addRq.flowModel.cron)) {
            return flowId;
        }
        try {
            CronExpression.validateExpression(addRq.flowModel.cron);
            schedulerEngine.addFlow(flowId, addRq.flowModel.cron);
        } catch (Exception e) {
            processEngine.deleteFlow(flowId);
            throw new ThainException(e);
        }
        return flowId;
    }

    /**
     * 更新flow
     */
    public boolean updateFlow(@NonNull UpdateFlowRq updateFlowRq, @NonNull List<JobModel> jobModelList) throws SchedulerException, ThainException, ParseException {

        if (StringUtils.isBlank(updateFlowRq.cron)) {
            return processEngine.updateFlow(updateFlowRq, jobModelList);
        }

        CronExpression.validateExpression(updateFlowRq.cron);
        schedulerEngine.addFlow(updateFlowRq.id, updateFlowRq.cron);
        try {
            processEngine.updateFlow(updateFlowRq, jobModelList);
            return true;
        } catch (Exception e) {
            schedulerEngine.addFlow(updateFlowRq.id, processEngine.getFlowCron(updateFlowRq.id));
            throw new ThainException(e);
        }
    }

    /**
     * 删除Flow
     */
    public void deleteFlow(long flowId) throws SchedulerException {
        schedulerEngine.deleteFlow(flowId);
        processEngine.deleteFlow(flowId);
    }

    /**
     * 触发某个Flow
     */
    public long startFlow(long flowId) throws ThainException, ThainRepeatExecutionException {
        return processEngine.startProcess(flowId);
    }

    public Map<String, String> getComponentDefineJsonList() {
        return processEngine.processEngineStorage.componentService.getComponentDefineJsonList();
    }

    public void pauseFlow(long flowId) throws ThainException {
        val flowDr = processEngine.processEngineStorage
                .flowDao.getFlow(flowId)
                .orElseThrow(() -> new ThainException(MessageFormat.format(NON_EXIST_FLOW, flowId)));
        try {
            processEngine.processEngineStorage.flowDao.pauseFlow(flowId);
            schedulerEngine.deleteFlow(flowId);
            if (StringUtils.isNotBlank(flowDr.modifyCallbackUrl)) {
                SendModifyUtils.sendPause(flowId, flowDr.modifyCallbackUrl);
            }
        } catch (Exception e) {
            log.error("", e);
            try {
                val jobModelList = processEngine.processEngineStorage
                        .jobDao.getJobs(flowId).orElseGet(Collections::emptyList);
                updateFlow(UpdateFlowRq.getInstance(flowDr), jobModelList);
            } catch (Exception ex) {
                log.error("", ex);
            }
            throw new ThainException(e);
        }
    }

    public void schedulingFlow(long flowId) throws ThainException, SchedulerException, IOException {
        val flowModel = processEngine.processEngineStorage
                .flowDao.getFlow(flowId)
                .orElseThrow(() -> new ThainException(MessageFormat.format(NON_EXIST_FLOW, flowId)));
        schedulerEngine.addFlow(flowModel.id, flowModel.cron);
        processEngine.processEngineStorage.flowDao
                .updateSchedulingStatus(flowModel.id, FlowSchedulingStatus.SCHEDULING);
        if (StringUtils.isNotBlank(flowModel.modifyCallbackUrl)) {
            SendModifyUtils.sendScheduling(flowId, flowModel.modifyCallbackUrl);
        }
    }

    public void killFlowExecution(long flowExecutionId) throws ThainException {
        val flowExecutionModel = processEngine.processEngineStorage.flowExecutionDao
                .getFlowExecution(flowExecutionId)
                .orElseThrow(() -> new ThainException("flowExecution id does not exist：" + flowExecutionId));
        if (FlowExecutionStatus.getInstance(flowExecutionModel.status) != FlowExecutionStatus.RUNNING) {
            throw new ThainException("flowExecution does not running: " + flowExecutionId);
        }
        processEngine.processEngineStorage.flowExecutionDao.killFlowExecution(flowExecutionId);
        processEngine.processEngineStorage.flowDao.killFlow(flowExecutionModel.flowId);
    }

    public void updateCron(long flowId, @Nullable String cron) throws ThainException, ParseException, SchedulerException, IOException {
        val flowDr = processEngine.processEngineStorage
                .flowDao.getFlow(flowId)
                .orElseThrow(() -> new ThainException(MessageFormat.format(NON_EXIST_FLOW, flowId)));
        val jobModelList = processEngine.processEngineStorage.jobDao.getJobs(flowId).orElseGet(Collections::emptyList);
        if (cron == null) {
            updateFlow(UpdateFlowRq.getInstance(flowDr), jobModelList);
        } else {
            updateFlow(UpdateFlowRq.getInstance(flowDr.toBuilder().cron(cron).build()), jobModelList);
        }
        if (StringUtils.isNotBlank(flowDr.modifyCallbackUrl)) {
            SendModifyUtils.sendScheduling(flowId, flowDr.modifyCallbackUrl);
        }
    }
}
