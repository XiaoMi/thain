/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core;

import com.alibaba.fastjson.JSON;
import com.xiaomi.thain.common.constant.FlowExecutionStatus;
import com.xiaomi.thain.common.constant.FlowSchedulingStatus;
import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.exception.ThainMissRequiredArgumentsException;
import com.xiaomi.thain.common.exception.scheduler.ThainSchedulerException;
import com.xiaomi.thain.common.model.dto.AddDto;
import com.xiaomi.thain.core.process.ProcessEngine;
import com.xiaomi.thain.core.process.ProcessEngineConfiguration;
import com.xiaomi.thain.core.scheduler.SchedulerEngine;
import com.xiaomi.thain.core.scheduler.SchedulerEngineConfiguration;
import com.xiaomi.thain.core.utils.SendModifyUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-16 下午8:38
 */
@Slf4j
public class ThainFacade {

    private static final String NON_EXIST_FLOW = "flow does not exist:{0}";
    @NonNull
    public final SchedulerEngine schedulerEngine;
    @NonNull
    private final ProcessEngine processEngine;

    private ThainFacade(@NonNull ProcessEngineConfiguration processEngineConfiguration,
                        @NonNull SchedulerEngineConfiguration schedulerEngineConfiguration)
            throws ThainSchedulerException, ThainMissRequiredArgumentsException, IOException, SQLException {
        processEngine = ProcessEngine.newInstance(processEngineConfiguration, this);
        schedulerEngine = SchedulerEngine.getInstance(schedulerEngineConfiguration, processEngine);
        schedulerEngine.start();
    }

    public static ThainFacade getInstance(ProcessEngineConfiguration processEngineConfiguration,
                                          SchedulerEngineConfiguration schedulerEngineConfiguration)
            throws ThainSchedulerException, ThainMissRequiredArgumentsException, IOException, SQLException {
        return new ThainFacade(processEngineConfiguration, schedulerEngineConfiguration);
    }

    /**
     * 新建任务, cron为空的则只部署，不调度, 这个flowJson是不含id的，如果含id也没用
     */
    public long addFlow(AddDto addDto) throws ThainException {
        val flowId = processEngine.addFlow(addDto.flowModel, addDto.jobModelList).orElseThrow(() -> new ThainException("failed to insert flow"));
        if (StringUtils.isBlank(addDto.flowModel.cron)) {
            return flowId;
        }
        try {
            CronExpression.validateExpression(addDto.flowModel.cron);
            schedulerEngine.addFlow(flowId, addDto.flowModel.cron);
        } catch (Exception e) {
            processEngine.deleteFlow(flowId);
            throw new ThainException(e);
        }
        return flowId;
    }

    public long updateFlow(String flowJson) throws SchedulerException, ThainException, ParseException {
        val addDto = JSON.parseObject(flowJson, AddDto.class);
        return updateFlow(addDto);
    }

    /**
     * 更新flow
     */
    public long updateFlow(AddDto addDto) throws SchedulerException, ThainException, ParseException {
        val flowModel = addDto.flowModel;
        val jobModelList = addDto.jobModelList;

        if (StringUtils.isBlank(flowModel.cron)) {
            processEngine.updateFlow(flowModel, jobModelList);
            return flowModel.id;
        }
        CronExpression.validateExpression(flowModel.cron);
        schedulerEngine.addFlow(flowModel.id, flowModel.cron);
        try {
            processEngine.updateFlow(flowModel, jobModelList);
            return flowModel.id;
        } catch (Exception e) {
            schedulerEngine.addFlow(flowModel.id, processEngine.getFlowCron(flowModel.id));
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
    public void startFlow(long flowId) throws ThainException {
        processEngine.startProcess(flowId);
    }

    public Map<String, String> getComponentDefineJsonList() {
        return processEngine.processEngineStorage.componentService.getComponentDefineJsonList();
    }

    public void pauseFlow(long flowId) throws ThainException {
        val flowModel = processEngine.processEngineStorage
                .flowDao.getFlow(flowId)
                .orElseThrow(() -> new ThainException(MessageFormat.format(NON_EXIST_FLOW, flowId)));
        try {
            processEngine.processEngineStorage.flowDao.pauseFlow(flowId);
            schedulerEngine.deleteFlow(flowId);
            if (Strings.isNotBlank(flowModel.modifyCallbackUrl)) {
                SendModifyUtils.sendPause(flowId, flowModel.modifyCallbackUrl);
            }
        } catch (Exception e) {
            log.error("", e);
            try {
                val jobModelList = processEngine.processEngineStorage
                        .jobDao.getJobs(flowId).orElseGet(Collections::emptyList);
                updateFlow(AddDto.builder().flowModel(flowModel).jobModelList(jobModelList).build());
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
        if (Strings.isNotBlank(flowModel.modifyCallbackUrl)) {
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
        val flowModel = processEngine.processEngineStorage
                .flowDao.getFlow(flowId)
                .orElseThrow(() -> new ThainException(MessageFormat.format(NON_EXIST_FLOW, flowId)));
        val jobModelList = processEngine.processEngineStorage.jobDao.getJobs(flowId).orElseGet(Collections::emptyList);
        if (cron == null) {
            updateFlow(AddDto.builder().flowModel(flowModel).jobModelList(jobModelList).build());
        } else {
            updateFlow(AddDto.builder().flowModel(flowModel.toBuilder().cron(cron).build()).jobModelList(jobModelList).build());
        }
        if (Strings.isNotBlank(flowModel.modifyCallbackUrl)) {
            SendModifyUtils.sendScheduling(flowId, flowModel.modifyCallbackUrl);
        }
    }
}
