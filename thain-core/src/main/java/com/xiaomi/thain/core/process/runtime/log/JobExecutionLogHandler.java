/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process.runtime.log;

import com.alibaba.fastjson.JSON;
import com.xiaomi.thain.core.constant.LogLevel;
import com.xiaomi.thain.core.dao.JobExecutionDao;
import com.xiaomi.thain.core.entity.LogEntity;
import com.xiaomi.thain.core.process.ProcessEngineStorage;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Date 19-5-20 下午5:09
 *
 * @author liangyongrui@xiaomi.com
 */
public class JobExecutionLogHandler {

    private final long jobExecutionId;
    @NonNull
    private final JobExecutionDao jobExecutionDao;

    @NonNull
    private final List<LogEntity> logs;

    private static final Map<Long, JobExecutionLogHandler> JOB_EXECUTION_LOG_HANDLER_MAP = new ConcurrentHashMap<>();

    private JobExecutionLogHandler(long jobExecutionId,
                                   @NonNull ProcessEngineStorage processEngineStorage) {
        this.jobExecutionId = jobExecutionId;
        this.jobExecutionDao = processEngineStorage.jobExecutionDao;
        logs = new CopyOnWriteArrayList<>();

    }

    public static JobExecutionLogHandler getInstance(long jobExecutionId,
                                                     @NonNull ProcessEngineStorage processEngineStorage) {
        return JOB_EXECUTION_LOG_HANDLER_MAP.computeIfAbsent(jobExecutionId,
                id -> new JobExecutionLogHandler(jobExecutionId, processEngineStorage));
    }

    public synchronized void add(@NonNull String content,
                                 @NonNull LogLevel logLevel) {
        logs.add(LogEntity.builder().level(logLevel.name()).content(content).timestamp(System.currentTimeMillis()).build());
        jobExecutionDao.updateLogs(jobExecutionId, JSON.toJSONString(logs));
    }

    public void close() {
        JOB_EXECUTION_LOG_HANDLER_MAP.remove(jobExecutionId);
    }
}
