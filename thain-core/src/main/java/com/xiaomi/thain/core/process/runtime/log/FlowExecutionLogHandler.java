/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process.runtime.log;

import com.alibaba.fastjson.JSON;
import com.xiaomi.thain.core.constant.LogLevel;
import com.xiaomi.thain.core.dao.FlowExecutionDao;
import com.xiaomi.thain.core.entity.LogEntity;
import com.xiaomi.thain.core.process.ProcessEngineStorage;
import lombok.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Date 19-5-20 下午5:09
 *
 * @author liangyongrui@xiaomi.com
 */
public class FlowExecutionLogHandler {

    private final long flowExecutionId;
    @NonNull
    private final FlowExecutionDao flowExecutionDao;
    @NonNull
    private final List<LogEntity> logs;

    private FlowExecutionLogHandler(long flowExecutionId,
                                    @NonNull ProcessEngineStorage processEngineStorage) {
        this.flowExecutionId = flowExecutionId;
        this.flowExecutionDao = processEngineStorage.flowExecutionDao;
        logs = new CopyOnWriteArrayList<>();

    }

    public static FlowExecutionLogHandler getInstance(long flowExecutionId,
                                                      @NonNull ProcessEngineStorage processEngineStorage) {
        return new FlowExecutionLogHandler(flowExecutionId, processEngineStorage);
    }

    /**
     * 插入一个log
     */
    public void addInfo(@NonNull String content) {
        addLog(LogLevel.INFO, content);
    }

    /**
     * 结束日志
     */
    public void endSuccess() {
        addInfo("executed successful");
    }

    public void endError(@NonNull String errorMessage) {
        addLog(LogLevel.ERROR, errorMessage);
    }

    private synchronized void addLog(@NonNull LogLevel logLevel, @NonNull String content) {
        logs.add(LogEntity.builder().level(logLevel.name()).content(content).timestamp(System.currentTimeMillis()).build());
        flowExecutionDao.updateLogs(flowExecutionId, JSON.toJSONString(logs));
    }
}
