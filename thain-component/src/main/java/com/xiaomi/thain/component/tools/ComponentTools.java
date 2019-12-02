/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.component.tools;

import lombok.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;

/**
 * Date 19-5-30 下午3:41
 *
 * @author liangyongrui@xiaomi.com
 */
public interface ComponentTools {

    /**
     * 发送邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 正文
     */
    void sendMail(String[] to, String subject, String content) throws IOException, MessagingException;

    /**
     * 保存当前节点产生的数据
     *
     * @param key 数据的key
     * @param value 数据的value
     */
    void putStorage(@NonNull final String key, @NonNull final Object value);

    /**
     * 获取流程已经产生的数据
     *
     * @param jobName 节点名称
     * @param key key
     * @param <T> 自动强制转换
     * @return 返回对应值的Optional
     */
    <T> Optional<T> getStorageValue(@NonNull final String jobName, @NonNull final String key);

    /**
     * 获取流程已经产生的数据
     *
     * @param jobName 节点名称
     * @param key key
     * @param defaultValue 默认值
     * @param <T> 自动强制转换
     * @return 返回对应值, 值不存在则返回defaultValue
     */
    <T> T getStorageValueOrDefault(@NonNull final String jobName, @NonNull final String key, @NonNull final T defaultValue);

    /**
     * 增加debug日志
     */
    void addDebugLog(String content);

    /**
     * 增加info日志
     */
    void addInfoLog(String content);

    /**
     * 增加warning日志
     */
    void addWarnLog(String content);

    /**
     * 增加error日志
     *
     * @param content
     */
    void addErrorLog(String content);

    /**
     * 发送http get 请求
     *
     * @param url url
     * @param data ?后面的
     */
    String httpGet(@NonNull String url, @NonNull Map<String, String> data) throws IOException;

    /**
     * 发送 http post 请求
     *
     * @param url url
     * @param headers headers
     * @param data data
     */
    String httpPost(@NonNull String url,
                    @NonNull Map<String, String> headers,
                    @NonNull Map<String, ?> data) throws IOException;

    /**
     * 获取当前的id
     */
    long getJobExecutionId();

    /**
     * 获取flow当前产生的全部结果
     */
    Map<String, Object> getStorage();
}
