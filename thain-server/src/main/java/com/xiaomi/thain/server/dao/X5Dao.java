/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.dao;

import com.xiaomi.thain.server.entity.X5Config;
import com.xiaomi.thain.server.mapper.X5Mapper;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

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

    public X5Config getX5Config(@NonNull String appId) {
        return x5Mapper.getX5Config(appId);
    }
}

