/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.sdk;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.xiaomi.thain.common.entity.ApiResult;
import com.xiaomi.thain.common.model.FlowModel;
import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.common.model.rq.UpdateJobPropertiesRq;
import com.xiaomi.thain.common.utils.HttpUtils;
import com.xiaomi.thain.common.utils.X5Utils;
import lombok.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author liangyongrui
 */
public class ThainClient {
    private final String appId;
    private final String appKey;
    private final String url;

    private static final String ADD_PATH = "/x5/flow/add";
    private static final String START_PATH = "/x5/flow/start";
    private static final String DELETE_PATH = "/x5/flow/delete";
    private static final String PAUSE_PATH = "/x5/flow/pause";
    private static final String SCHEDULE_PATH = "/x5/flow/schedule";
    private static final String KILL_PATH = "/x5/flow-execution/kill";
    private static final String ALL_INFO_PATH = "/x5/flow-execution/all-info";
    private static final String ALL_EXECUTION_INFO = "/x5/flow-execution/infos";
    private static final String UPDATE_JOB_PROPERTIES = "/x5/job/update-properties";

    
    private static final String KEY_FLOW_ID = "flowId";
    
    public static ThainClient getInstance(@NonNull String appId, @NonNull String appKey, @NonNull String host) {
        return new ThainClient(appId, appKey, host);
    }

    private ThainClient(@NonNull String appId, @NonNull String appKey, String host) {
        this.appId = appId;
        this.appKey = appKey;
        this.url = host;
    }


    /**
     * 存在的key，就更新
     * 不存在的key，就新增
     */
    public ApiResult updateJobProperties(long flowId,
                                         @NonNull String jobName,
                                         @NonNull Map<String, String> modifyProperties) throws IOException {
        return buildRequest(url + UPDATE_JOB_PROPERTIES, JSON.toJSONString(UpdateJobPropertiesRq.builder()
                .flowId(flowId)
                .jobName(jobName)
                .modifyProperties(modifyProperties)
                .build()));
    }

    /**
     * 创建或修改flow
     */
    public ApiResult addFlow(@NonNull FlowModel flowModel, @NonNull List<JobModel> jobModelList) throws
            IOException {
        return buildRequest(url + ADD_PATH, JSON.toJSONString(ImmutableMap.of("flowModel", flowModel, "jobModelList", jobModelList)));
    }

    /**
     * 立即触发一次
     */
    public ApiResult startFlow(long flowId) throws IOException {
        return buildRequest(url + START_PATH, JSON.toJSONString(ImmutableMap.of(KEY_FLOW_ID, flowId)));
    }

    public ApiResult deleteFlow(long flowId) throws IOException {
        return buildRequest(url + DELETE_PATH, JSON.toJSONString(ImmutableMap.of(KEY_FLOW_ID, flowId)));
    }

    public ApiResult killFlowExecution(long flowExecutionId) throws IOException {
        return buildRequest(url + KILL_PATH, JSON.toJSONString(ImmutableMap.of("flowExecutionId", flowExecutionId)));
    }

    public ApiResult getFlowExecutionInfo(long flowExecutionId) throws IOException {
        return buildRequest(url + ALL_INFO_PATH, JSON.toJSONString(ImmutableMap.of("flowExecutionId", flowExecutionId)));
    }

    public ApiResult pauseFlow(long flowId) throws IOException {
        return buildRequest(url + PAUSE_PATH, JSON.toJSONString(ImmutableMap.of(KEY_FLOW_ID, flowId)));
    }

    public ApiResult scheduleFlow(long flowId, @NonNull String cron) throws IOException {
        return buildRequest(url + SCHEDULE_PATH, JSON.toJSONString(ImmutableMap.of(KEY_FLOW_ID, flowId, "cron", cron)));
    }

    public ApiResult scheduleFlow(long flowId) throws IOException {
        return buildRequest(url + SCHEDULE_PATH, JSON.toJSONString(ImmutableMap.of(KEY_FLOW_ID, flowId)));
    }

    /**
     * get FlowExecution by flowId
     *
     * @param flowId   flowId
     * @param page     page
     * @param pageSize pageSize
     * @return {@link ApiResult}
     */
    public ApiResult getFlowExecutionList(long flowId, int page, int pageSize) throws IOException {
        return buildRequest(url + ALL_EXECUTION_INFO, JSON.toJSONString(ImmutableMap.of(KEY_FLOW_ID, flowId, "page", page, "pageSize", pageSize)));
    }

    private ApiResult buildRequest(@NonNull String url, @NonNull String body) throws IOException {
        String result = HttpUtils.postForm(url, X5Utils.buildX5Request(appId, appKey, body));
        return JSON.parseObject(result, ApiResult.class);
    }

}
