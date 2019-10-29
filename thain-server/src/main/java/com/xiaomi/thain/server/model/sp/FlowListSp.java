/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.model.sp;

import com.xiaomi.thain.server.model.rq.FlowListRq;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * @author liangyongrui@xiaomi.com
 * @date 18-12-29 上午11:59
 */
public class FlowListSp {
    public final int offset;
    public final int limit;
    @Nullable
    public final Long flowId;
    @Nullable
    public final Integer lastRunStatus;
    @Nullable
    public final String queryUsername;
    @Nullable
    public final String sortKey;

    public final boolean sortOrderDesc;

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

    @Nullable
    public final Set<String> appIds;

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;

    public static FlowListSp getInstance(FlowListRq flowListRq) {
        return new FlowListSp(flowListRq);
    }

    public static FlowListSp getInstance(FlowListRq flowListRq, String username, Set<String> ids) {
        return new FlowListSp(flowListRq, username, ids);
    }

    private FlowListSp(FlowListRq flowListRq, @Nullable String username, @Nullable Set<String> appIds) {
        queryUsername = username;
        Integer page = flowListRq.page;
        if (page == null || page < 1) {
            page = DEFAULT_PAGE;
        }
        Integer pageSize = flowListRq.pageSize;
        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        lastRunStatus = flowListRq.lastRunStatus;
        offset = (page - 1) * pageSize;
        limit = pageSize;
        flowId = flowListRq.flowId;
        sortKey = flowListRq.sortKey;
        sortOrderDesc = flowListRq.sortOrderDesc != null && flowListRq.sortOrderDesc;
        flowName = flowListRq.flowName;
        scheduleStatus = flowListRq.scheduleStatus;
        updateTime = flowListRq.updateTime;
        this.appIds = appIds;
        this.searchApp = flowListRq.searchApp;
        this.createUser = flowListRq.createUser;
    }

    private FlowListSp(FlowListRq flowListRq) {
        this(flowListRq, null, null);
    }
}
