/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.entity.response;

import lombok.Builder;
import lombok.NonNull;

/**
 * Date 2019/7/31 下午5:12
 *
 * @author liangyongrui@xiaomi.com
 */
@Builder(toBuilder = true)
public class StatusHistoryCount {
    public final int status;
    public final long count;
    @NonNull
    public final String time;
}
