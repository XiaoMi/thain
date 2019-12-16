/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.process.runtime.notice;

import com.google.common.collect.ImmutableMap;
import com.xiaomi.thain.common.constant.FlowLastRunStatus;
import com.xiaomi.thain.common.utils.HttpUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.thain.common.constant.FlowLastRunStatus.*;

/**
 * Date 19-5-21 上午10:03
 * http 通知
 *
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
public class FlowHttpNotice {
    @NonNull
    private final String url;
    private final long flowId;
    private final long flowExecutionId;

    private static final String CODE_KEY = "code";
    private static final String MESSAGE_KEY = "message";
    private static final String FLOW_ID = "flowId";
    private static final String FLOW_EXECUTION_ID = "flowExecutionId";

    private FlowHttpNotice(@NonNull String url, long flowId, long flowExecutionId) {
        this.url = url;
        this.flowId = flowId;
        this.flowExecutionId = flowExecutionId;
    }

    public static FlowHttpNotice getInstance(@NonNull String url, long flowId, long executionId) {
        return new FlowHttpNotice(url, flowId, executionId);
    }

    public void sendStart() {
        checkAndPost(RUNNING, "");
    }

    public void sendError(@NonNull String errorMessage) {
        checkAndPost(ERROR, errorMessage);
    }

    public void sendKilled() {
        checkAndPost(KILLED, "");
    }

    public void sendAutoKilled() {
        checkAndPost(AUTO_KILLED, "");
    }


    public void sendSuccess() {
        checkAndPost(SUCCESS, "");
    }

    private void checkAndPost(@NonNull FlowLastRunStatus status, @NonNull String message) {
        if (url.trim().length() == 0) {
            return;
        }
        try {
            HttpUtils.postForm(url, ImmutableMap.of(
                    FLOW_ID, flowId + "",
                    FLOW_EXECUTION_ID, flowExecutionId + "",
                    CODE_KEY, status.code + "",
                    MESSAGE_KEY, message
            ));
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
