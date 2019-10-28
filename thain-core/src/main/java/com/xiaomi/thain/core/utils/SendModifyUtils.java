/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.utils;

import com.google.common.collect.ImmutableMap;
import com.xiaomi.thain.common.utils.HttpUtils;
import lombok.NonNull;

import java.io.IOException;

/**
 * Date 19-7-10 下午2:07
 *
 * @author liangyongrui@xiaomi.com
 */
public class SendModifyUtils {

    private static final int PAUSE = 1;
    private static final int SCHEDULING = 2;

    private SendModifyUtils() {

    }

    public static void sendPause(long flowId, @NonNull String modifyCallbackUrl) throws IOException {
        HttpUtils.post(modifyCallbackUrl, ImmutableMap.of("flowId", flowId, "status", PAUSE));
    }

    public static void sendScheduling(long flowId, @NonNull String modifyCallbackUrl) throws IOException {
        HttpUtils.post(modifyCallbackUrl, ImmutableMap.of("flowId", flowId, "status", SCHEDULING));
    }
}
