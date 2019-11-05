/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process.component.tools.impl;

import com.xiaomi.thain.common.model.JobModel;
import com.xiaomi.thain.common.utils.HttpUtils;
import com.xiaomi.thain.component.tools.ComponentTools;
import com.xiaomi.thain.core.constant.LogLevel;
import com.xiaomi.thain.core.process.ProcessEngineStorage;
import com.xiaomi.thain.core.process.runtime.log.JobExecutionLogHandler;
import com.xiaomi.thain.core.process.runtime.storage.FlowExecutionStorage;
import com.xiaomi.thain.core.process.service.MailService;
import lombok.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;

/**
 * Date 19-5-30 下午4:31
 *
 * @author liangyongrui@xiaomi.com
 */
public class ComponentToolsImpl implements ComponentTools {

    @NonNull
    private final JobModel jobModel;
    @NonNull
    private final FlowExecutionStorage flowExecutionStorage;
    @NonNull
    private final JobExecutionLogHandler log;
    @NonNull
    private final MailService mailService;

    private final long jobExecutionId;

    public ComponentToolsImpl(@NonNull JobModel jobModel,
                              long jobExecutionId,
                              long flowExecutionId,
                              @NonNull ProcessEngineStorage processEngineStorage) {
        this.jobExecutionId = jobExecutionId;
        this.mailService = processEngineStorage.mailService;
        this.jobModel = jobModel;
        this.flowExecutionStorage = FlowExecutionStorage.getInstance(flowExecutionId);
        this.log = JobExecutionLogHandler.getInstance(jobExecutionId, processEngineStorage);
    }

    @Override
    public void sendMail(@NonNull String[] to, @NonNull String subject, @NonNull String content) throws IOException, MessagingException {
        mailService.send(to, subject, content);
    }

    /**
     * 保存当前节点产生的数据
     *
     * @param key 数据的key
     * @param value 数据的value
     */
    @Override
    public void putStorage(@NonNull final String key, @NonNull final Object value) {
        flowExecutionStorage.put(jobModel.name, key, value);
    }

    /**
     * 获取流程已经产生的数据
     *
     * @param jobName 节点名称
     * @param key key
     * @param <T> 自动强制转换
     * @return 返回对应值的Optional
     */
    @Override
    public <T> Optional<T> getStorageValue(@NonNull final String jobName, @NonNull final String key) {
        return flowExecutionStorage.get(jobName, key);
    }

    /**
     * 获取流程已经产生的数据
     *
     * @param jobName 节点名称
     * @param key key
     * @param defaultValue 默认值
     * @param <T> 自动强制转换
     * @return 返回对应值, 值不存在则返回defaultValue
     */
    @Override
    public <T> T getStorageValueOrDefault(@NonNull final String jobName, @NonNull final String key, @NonNull final T defaultValue) {
        final Optional<T> optional = getStorageValue(jobName, key);
        return optional.orElse(defaultValue);
    }

    /**
     * 增加debug日志
     */
    @Override
    public void addDebugLog(@NonNull String content) {
        log.add(content, LogLevel.DEBUG);
    }

    /**
     * 增加info日志
     */
    @Override
    public void addInfoLog(@NonNull String content) {
        log.add(content, LogLevel.INFO);
    }

    /**
     * 增加warning日志
     */
    @Override
    public void addWarnLog(@NonNull String content) {
        log.add(content, LogLevel.WARN);
    }

    /**
     * 增加error日志
     */
    @Override
    public void addErrorLog(@NonNull String content) {
        log.add(content, LogLevel.ERROR);
    }

    @Override
    public String httpGet(@NonNull String url, @NonNull Map<String, String> data) throws IOException {
        return HttpUtils.get(url, data);
    }

    @Override
    public String httpPost(@NonNull String url, @NonNull Map<String, String> headers, @NonNull Map<String, ?> data)
            throws IOException {
        return HttpUtils.post(url, headers, data);
    }

    @Override
    public long getJobExecutionId() {
        return jobExecutionId;
    }

    @Override
    public Map<String, Object> getStorage() {
        return flowExecutionStorage.storageMap;
    }

}
