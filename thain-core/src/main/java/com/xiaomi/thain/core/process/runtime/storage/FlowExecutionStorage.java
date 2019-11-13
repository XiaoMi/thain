/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.core.process.runtime.storage;

import lombok.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Date 19-5-17 上午9:43
 *
 * 保存流程产生的中间结果
 *
 * @author liangyongrui@xiaomi.com
 */
public class FlowExecutionStorage {

    private static final Map<Long, FlowExecutionStorage> FLOW_EXECUTION_STORAGE_MAP = new ConcurrentHashMap<>();

    @NonNull
    public final Map<String, Object> storageMap;
    @NonNull
    private final Set<String> finishJob;

    private FlowExecutionStorage(long flowExecutionId) {
        storageMap = new ConcurrentHashMap<>();
        finishJob = new HashSet<>();
    }

    public static FlowExecutionStorage getInstance(final long flowExecutionId) {
        return FLOW_EXECUTION_STORAGE_MAP.computeIfAbsent(flowExecutionId, FlowExecutionStorage::new);
    }

    public void put(@NonNull final String jobName, @NonNull final String key, @NonNull final Object value) {
        storageMap.put(jobName + "." + key, value);
    }

    /**
     * 添加到完成列表
     */
    public void addFinishJob(@NonNull String jobName) {
        finishJob.add(jobName);
    }

    /**
     * 判断Job name 是否finish
     */
    public boolean finished(@NonNull String jobName) {
        return finishJob.contains(jobName);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(@NonNull final String jobName, @NonNull final String key) {
        return Optional.ofNullable((T) storageMap.get(jobName + "." + key));
    }

    public static void drop(@NonNull Long flowExecutionId) {
        FLOW_EXECUTION_STORAGE_MAP.remove(flowExecutionId);
    }
}
