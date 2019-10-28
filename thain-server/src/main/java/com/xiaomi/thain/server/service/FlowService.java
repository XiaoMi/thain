/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.service;

import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.model.FlowModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.server.entity.query.FlowListQuery;
import lombok.NonNull;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * @author liangyongrui
 */
@Service
public interface FlowService {

    List<FlowModel> getFlowList(@NonNull FlowListQuery flowListQuery);

    Long getFlowListCount(@NonNull FlowListQuery flowListQuery);

    /**
     * 创建或更新任务
     *
     * @param appId app id
     * @return flow id
     */
    long add(@NonNull FlowModel flowModel, @NonNull List<JobModel> jobModelList, @NonNull String appId)
            throws ThainException, ParseException, SchedulerException;

    /**
     * 删除
     */
    boolean delete(long flowId) throws SchedulerException;

    boolean start(long flowId) throws ThainException;

    FlowModel getFlow(long flowId);

    List<JobModel> getJobModelList(long flowId);

    Map<String, String> getComponentDefineStringMap();

    void scheduling(long flowId) throws ThainException, SchedulerException, IOException;

    void pause(long flowId) throws ThainException;

    void updateCron(long flowId, @Nullable String cron) throws ThainException, ParseException, SchedulerException, IOException;
}
