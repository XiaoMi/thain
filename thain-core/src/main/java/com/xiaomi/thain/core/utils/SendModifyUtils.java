/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.utils;

import com.google.common.collect.ImmutableMap;
import com.xiaomi.thain.common.utils.HttpUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.conn.HttpHostConnectException;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Date 19-7-10 下午2:07
 *
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
public class SendModifyUtils {

    private static final int PAUSE = 1;
    private static final int SCHEDULING = 2;

    private SendModifyUtils() {

    }

    public static void sendPause(long flowId, @NonNull String modifyCallbackUrl) throws IOException {
        try {
            HttpUtils.postForm(modifyCallbackUrl, ImmutableMap.of("flowId", flowId, "status", PAUSE));
        } catch (HttpHostConnectException e) {
            log.warn(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public static void sendScheduling(long flowId, @NonNull String modifyCallbackUrl) throws IOException {
        try {
            HttpUtils.postForm(modifyCallbackUrl, ImmutableMap.of("flowId", flowId, "status", SCHEDULING));
        } catch (HttpHostConnectException | UnknownHostException e) {
            log.warn(ExceptionUtils.getRootCauseMessage(e));
        }
    }
}
