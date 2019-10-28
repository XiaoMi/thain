/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service;

import com.xiaomi.thain.server.entity.X5Config;
import com.xiaomi.thain.server.entity.request.X5ConfigRequest;
import com.xiaomi.thain.server.entity.response.X5ConfigResponse;
import lombok.NonNull;

import java.util.List;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-7 上午11:40
 */
public interface X5Service {

    X5Config getX5Config(@NonNull String appid);

    /**
     * get all x5Configs
     * @return
     */
    List<X5ConfigResponse> getAllConfigs();

    /**
     * delete x5Config
     * @param appId
     */
    void deleteX5Config(@NonNull String appId);

    /**
     * insert x5Config
     * @param x5ConfigRequest
     * @return
     */
    boolean insertX5Config(@NonNull X5ConfigRequest x5ConfigRequest);

    /**
     * update X5Config
     * @return
     * @param x5ConfigRequest
     */
    boolean updateX5Config(@NonNull  X5ConfigRequest x5ConfigRequest);
}

