/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.process.runtime.notice;

import com.google.common.collect.ImmutableMap;

import static com.xiaomi.thain.common.constant.HttpCallbackStatus.ERROR;
import static com.xiaomi.thain.common.constant.HttpCallbackStatus.KILLED;
import static com.xiaomi.thain.common.constant.HttpCallbackStatus.PAUSE;
import static com.xiaomi.thain.common.constant.HttpCallbackStatus.START;
import static com.xiaomi.thain.common.constant.HttpCallbackStatus.SUCCESS;

import com.xiaomi.thain.common.utils.HttpUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * Date 19-5-21 上午10:03
 * http 通知
 *
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
public class HttpNotice {
    @NonNull
    private final String url;
    private final long flowId;
    private final long flowExecutionId;

    private static final String CODE_KEY = "code";
    private static final String MESSAGE_KEY = "message";
    private static final String FLOW_ID = "flowId";
    private static final String FLOW_EXECUTION_ID = "flowExecutionId";

    private HttpNotice(@NonNull String url, long flowId, long flowExecutionId) {
        this.url = url;
        this.flowId = flowId;
        this.flowExecutionId = flowExecutionId;
    }

    public static HttpNotice getInstance(@NonNull String url, long flowId, long executionId) {
        return new HttpNotice(url, flowId, executionId);
    }

    public void sendStart() {
        checkAndPost(ImmutableMap.of(
                FLOW_ID, flowId + "",
                FLOW_EXECUTION_ID, flowExecutionId + "",
                CODE_KEY, START.code + ""
        ));
    }

    public void sendError(@NonNull String errorMessage) {
        checkAndPost(ImmutableMap.of(
                FLOW_ID, flowId + "",
                FLOW_EXECUTION_ID, flowExecutionId + "",
                CODE_KEY, ERROR.code + "",
                MESSAGE_KEY, errorMessage
        ));
    }

    public void sendKilled() {
        checkAndPost(ImmutableMap.of(
                FLOW_ID, flowId + "",
                FLOW_EXECUTION_ID, flowExecutionId + "",
                CODE_KEY, KILLED.code + ""));
    }

    public void sendPause() {
        checkAndPost(ImmutableMap.of(
                FLOW_ID, flowId + "",
                FLOW_EXECUTION_ID, flowExecutionId + "",
                CODE_KEY, PAUSE.code + ""));
    }

    public void sendSuccess() {
        checkAndPost(ImmutableMap.of(
                FLOW_ID, flowId + "",
                FLOW_EXECUTION_ID, flowExecutionId + "",
                CODE_KEY, SUCCESS.code + ""));
    }

    private void checkAndPost(@NonNull Map<String, String> data) {
        if (url.trim().length() == 0) {
            return;
        }
        try {
            HttpUtils.post(url, data);
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }
}
