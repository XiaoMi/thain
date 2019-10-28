/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service.impl;

import com.xiaomi.thain.server.dao.X5Dao;
import com.xiaomi.thain.server.entity.X5Config;
import com.xiaomi.thain.server.service.X5Service;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        return x5Dao.getX5Config(appId);
    }

}

