/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.service;

import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author liangyongrui@xiaomi.com
 * @date 18-12-6 上午11:37
 */
public interface PermissionService {

    /**
     * 判断username 是否有权限访问、操作flowId指定的flow
     *
     * @param appIds appId 列表
     * @param userId 用户名
     * @param flowId flow id
     * @return 有权限返回true
     */
    boolean getFlowAccessible(long flowId, @NonNull String userId, @Nullable Set<String> appIds);

    /**
     * 判断username 是否有权限访问、操作flowId指定的flow
     *
     * @param appId  appId
     * @param flowId flow id
     * @return 有权限返回true
     */
    boolean getFlowAccessible(long flowId, @NonNull String appId);

    /**
     * 判断username 是否有权限访问、操作flowExecutionId指定的flowExecution
     *
     * @param appIds          appId 列表
     * @param userId          用户名
     * @param flowExecutionId flowExecutionId
     * @return 有权限返回true
     */
    boolean getFlowExecutionAccessible(long flowExecutionId, @NonNull String userId, @Nullable Set<String> appIds);

    /**
     * 判断username 是否有权限访问、操作flowExecutionId指定的flowExecution
     *
     * @param appId           appId
     * @param flowExecutionId flowExecutionId
     * @return 有权限返回true
     */
    boolean getFlowExecutionAccessible(long flowExecutionId, @NonNull String appId);
}
