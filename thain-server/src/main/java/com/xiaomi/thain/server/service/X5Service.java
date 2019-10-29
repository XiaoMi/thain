/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service;

import com.xiaomi.thain.server.model.X5Config;
import com.xiaomi.thain.server.model.rq.X5ConfigRq;
import com.xiaomi.thain.server.model.rp.X5ConfigResponse;
import lombok.NonNull;

import java.util.List;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-7 上午11:40
 */
public interface X5Service {
    /**
     * get Config By AppId
     *
     * @param appid appId
     * @return see {@link X5Config}
     */
    X5Config getX5Config(@NonNull String appid);

    /**
     * get All Configs
     *
     * @return see {@link X5ConfigResponse}
     */
    List<X5ConfigResponse> getAllConfigs();

    /**
     * delete Config
     *
     * @param appId AppId
     */
    void deleteX5Config(@NonNull String appId);

    /**
     * insert Config
     *
     * @param x5ConfigRq see {@link X5ConfigRq}
     * @return false-failure true-success
     */
    boolean insertX5Config(@NonNull X5ConfigRq x5ConfigRq);

    /**
     * update Config
     *
     * @param x5ConfigRq see {@link X5ConfigRq}
     * @return false-failure true-success
     */
    boolean updateX5Config(@NonNull X5ConfigRq x5ConfigRq);
}

