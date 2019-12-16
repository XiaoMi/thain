/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.dao;

import com.xiaomi.thain.server.model.X5Config;
import com.xiaomi.thain.server.model.dp.X5ConfigDp;
import com.xiaomi.thain.core.model.dr.X5ConfigDr;
import com.xiaomi.thain.server.mapper.X5Mapper;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-7 上午11:30
 */
@Repository
public class X5Dao {

    @NonNull
    private final X5Mapper x5Mapper;

    public X5Dao(@NonNull X5Mapper x5Mapper) {
        this.x5Mapper = x5Mapper;
    }

    public Optional<X5Config> getX5Config(@NonNull String appId) {
        return x5Mapper.getX5Config(appId);
    }

    public List<X5ConfigDr> getAllX5Config() {
        return x5Mapper.getAllX5Config();
    }

    public void addX5Config(@NonNull X5ConfigDp x5ConfigDp) {
        x5Mapper.addOrUpdateX5Config(x5ConfigDp);
    }

    public void deleteX5Config(@NonNull String appId) {
        x5Mapper.deleteX5Config(appId);
    }

    public void updateX5Config(@NonNull X5ConfigDp x5ConfigDp) {
        x5Mapper.updateX5Config(x5ConfigDp);
    }
}

