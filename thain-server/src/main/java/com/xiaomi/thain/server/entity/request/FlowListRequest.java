/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.entity.request;

import lombok.AllArgsConstructor;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author liangyongrui@xiaomi.com
 * @date 18-12-29 上午11:15
 */
@AllArgsConstructor
public class FlowListRequest {
    @Nullable
    public final Integer page;
    @Nullable
    public final Integer pageSize;
    @Nullable
    public final Long flowId;
    @Nullable
    public final Integer lastRunStatus;
    @Nullable
    public final String sortKey;
    @Nullable
    public final Boolean sortOrderDesc;
    @Nullable
    public final String flowName;
    @Nullable
    public final Integer scheduleStatus;
    @Nullable
    public final List<Long> updateTime;
    @Nullable
    public final String searchApp;
    @Nullable
    public final String createUser;
}
