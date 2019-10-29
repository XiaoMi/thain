/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.server.dao.X5Dao;
import com.xiaomi.thain.server.entity.X5Config;
import com.xiaomi.thain.server.entity.dp.X5ConfigDp;
import com.xiaomi.thain.server.entity.request.X5ConfigRequest;
import com.xiaomi.thain.server.entity.response.X5ConfigResponse;
import com.xiaomi.thain.server.service.X5Service;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-7 上午11:01
 */
@Slf4j
@Service
public class X5ServiceImpl implements X5Service {

    @NonNull
    private final X5Dao x5Dao;

    public X5ServiceImpl(@NonNull X5Dao x5Dao) {
        this.x5Dao = x5Dao;
    }

    @Override
    public X5Config getX5Config(@NonNull String appId) {
        return x5Dao.getX5Config(appId).orElseThrow(() -> new ThainRuntimeException("AppId is not Existed"));
    }

    @Override
    public List<X5ConfigResponse> getAllConfigs() {
        val configs = x5Dao.getAllX5Config();
        val x5Configs = new LinkedList<X5ConfigResponse>();
        configs.forEach(config -> {
            val principals = JSON.parseArray(config.principal, String.class);
            x5Configs.add(X5ConfigResponse.builder()
                    .appId(config.appId)
                    .appKey(config.appKey)
                    .appName(config.appName)
                    .description(config.description)
                    .createTime(config.createTime.getTime())
                    .principals(principals).build());
        });
        return x5Configs;
    }

    @Override
    public void deleteX5Config(@NonNull String appId) {
        x5Dao.deleteX5Config(appId);
    }

    @Override
    public boolean insertX5Config(@NonNull X5ConfigRequest x5ConfigRequest) {
        if (x5ConfigRequest.principals.isEmpty()) {
            return false;
        }
        if (!x5Dao.getX5Config(x5ConfigRequest.appId).isPresent()) {
            x5Dao.addX5Config(X5ConfigDp.builder()
                    .appId(x5ConfigRequest.appId)
                    .appKey(x5ConfigRequest.appKey)
                    .appName(x5ConfigRequest.appName)
                    .description(x5ConfigRequest.description)
                    .principal(JSON.toJSONString(x5ConfigRequest.principals))
                    .build());
            return true;
        }
        return false;
    }

    @Override
    public boolean updateX5Config(@NonNull X5ConfigRequest x5ConfigRequest) {
        if (x5Dao.getX5Config(x5ConfigRequest.appId).isPresent()) {
            x5Dao.updateX5Config(X5ConfigDp.builder()
                    .appId(x5ConfigRequest.appId)
                    .appKey(x5ConfigRequest.appKey)
                    .appName(x5ConfigRequest.appName)
                    .description(x5ConfigRequest.description)
                    .principal(JSON.toJSONString(x5ConfigRequest.principals))
                    .build());
            return true;
        }
        return false;
    }
}

