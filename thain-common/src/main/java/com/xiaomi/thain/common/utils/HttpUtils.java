/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.common.utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author liangyongrui
 */
@Slf4j
public class HttpUtils {

    private HttpUtils() {
    }

    private static final PoolingHttpClientConnectionManager CM;

    static {
        final int timeout = 5 * 60 * 1000;
        final int maxTotalPool = 300;
        final int maxConPerRoute = 300;
        CM = new PoolingHttpClientConnectionManager();
        CM.setMaxTotal(maxTotalPool);
        CM.setDefaultMaxPerRoute(maxConPerRoute);
        CM.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(timeout).build());
    }

    public static String post(@NonNull String url, @NonNull Map<String, ?> data) throws IOException {
        return post(url, Collections.emptyMap(), data);
    }

    public static String post(@NonNull String url,
                              @NonNull Map<String, String> headers,
                              @NonNull Map<String, ?> data) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPost.addHeader(entry.getKey(), entry.getValue());
        }
        List<NameValuePair> formParams = new ArrayList<>();
        data.keySet().forEach(key -> formParams.add(new BasicNameValuePair(key, MapUtils.getString(data, key))));
        UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        httpPost.setEntity(urlEntity);
        httpPost.setConfig(RequestConfig.custom().build());
        val httpClient = HttpClients.custom().setConnectionManager(CM).build();
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            if (Objects.nonNull(entity)) {
                return EntityUtils.toString(entity, UTF_8);
            }
        }
        return "";
    }

    public static String get(@NonNull String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        val httpClient = HttpClients.custom().setConnectionManager(CM).build();
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            if (Objects.nonNull(entity)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
        return "";
    }

    public static String get(@NonNull String url, @NonNull Map<String, String> data) throws IOException {
        String condition = data.entrySet().stream().map(t -> t.getKey() + "=" + t.getValue()).collect(Collectors.joining("&"));
        String finalUrl;
        if (condition.length() == 0) {
            finalUrl = url;
        } else {
            if (url.contains("?")) {
                finalUrl = url + "&" + condition;
            } else {
                finalUrl = url + "?" + condition;
            }
        }
        return get(finalUrl);
    }

}
