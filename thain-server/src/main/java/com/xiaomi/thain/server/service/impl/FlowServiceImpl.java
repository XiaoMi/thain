/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.service.impl;

import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.common.model.FlowModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.common.model.rq.AddRq;
import com.xiaomi.thain.core.ThainFacade;
import com.xiaomi.thain.server.dao.FlowDao;
import com.xiaomi.thain.server.model.sp.FlowListSp;
import com.xiaomi.thain.server.service.FlowService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author liangyongrui
 */
@Service
@Log4j2
public class FlowServiceImpl implements FlowService {

    @NonNull
    private final FlowDao flowDao;
    @NonNull
    private final ThainFacade thainFacade;

    @Autowired
    public FlowServiceImpl(@NonNull FlowDao flowDao,
                           @NonNull ThainFacade thainFacade) {
        this.flowDao = flowDao;
        this.thainFacade = thainFacade;
    }

    @Override
    public List<FlowModel> getFlowList(@NonNull FlowListSp flowListSp) {
        return flowDao.getFlowList(flowListSp);
    }

    @Override
    public Long getFlowListCount(@NonNull FlowListSp flowListSp) {
        return flowDao.getFlowListCount(flowListSp);
    }

    private long update(@NonNull AddRq addRq) throws ThainException, ParseException, SchedulerException {
        return thainFacade.updateFlow(addRq);
    }

    @Override
    public long add(@NonNull FlowModel flowModel, @NonNull List<JobModel> jobModelList, String appId)
            throws ThainException, ParseException, SchedulerException {
        if (!flowModel.slaKill || flowModel.slaDuration == 0) {
            flowModel.toBuilder().slaKill(true).slaDuration(3L * 60 * 60).build();
        }
        val addDto = AddRq.builder().flowModel(flowModel).jobModelList(jobModelList).build();
        if (flowDao.flowExist(flowModel.id)) {
            return update(addDto);
        }
        long flowId = thainFacade.addFlow(addDto);
        flowDao.updateAppId(flowId, appId);
        return flowId;
    }

    @Override
    public boolean delete(long flowId) throws SchedulerException {
        thainFacade.deleteFlow(flowId);
        return true;
    }

    @Override
    public boolean start(long flowId) throws ThainException {
        thainFacade.startFlow(flowId);
        return true;
    }

    @Override
    public FlowModel getFlow(long flowId) {
        return flowDao.getFlow(flowId).orElseThrow(() -> new ThainRuntimeException("Flow does not exist, flow Id:" + flowId));
    }

    @Override
    public List<JobModel> getJobModelList(long flowId) {
        return flowDao.getJobModelList(flowId);
    }

    @Override
    public Map<String, String> getComponentDefineStringMap() {
        return thainFacade.getComponentDefineJsonList();
    }

    @Override
    public void scheduling(long flowId) throws ThainException, SchedulerException, IOException {
        thainFacade.schedulingFlow(flowId);
    }

    @Override
    public void updateCron(long flowId, @Nullable String cron)
            throws ThainException, ParseException, SchedulerException, IOException {
        thainFacade.updateCron(flowId, cron);
    }

    @Override
    public void pause(long flowId) throws ThainException {
        thainFacade.pauseFlow(flowId);
    }
}
