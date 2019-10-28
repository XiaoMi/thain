/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.entity.query;

import com.xiaomi.thain.server.entity.request.FlowListRequest;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * @author liangyongrui@xiaomi.com
 * @date 18-12-29 上午11:59
 */
public class FlowListQuery {
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

    public static FlowListQuery getInstance(FlowListRequest flowListRequest) {
        return new FlowListQuery(flowListRequest);
    }

    public static FlowListQuery getInstance(FlowListRequest flowListRequest, String username, Set<String> ids) {
        return new FlowListQuery(flowListRequest, username, ids);
    }

    private FlowListQuery(FlowListRequest flowListRequest, @Nullable String username, @Nullable Set<String> appIds) {
        queryUsername = username;
        Integer page = flowListRequest.page;
        if (page == null || page < 1) {
            page = DEFAULT_PAGE;
        }
        Integer pageSize = flowListRequest.pageSize;
        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        lastRunStatus = flowListRequest.lastRunStatus;
        offset = (page - 1) * pageSize;
        limit = pageSize;
        flowId = flowListRequest.flowId;
        sortKey = flowListRequest.sortKey;
        sortOrderDesc = flowListRequest.sortOrderDesc != null && flowListRequest.sortOrderDesc;
        flowName = flowListRequest.flowName;
        scheduleStatus = flowListRequest.scheduleStatus;
        updateTime = flowListRequest.updateTime;
        this.appIds = appIds;
        this.searchApp = flowListRequest.searchApp;
        this.createUser = flowListRequest.createUser;
    }

    private FlowListQuery(FlowListRequest flowListRequest) {
        this(flowListRequest, null, null);
    }
}
