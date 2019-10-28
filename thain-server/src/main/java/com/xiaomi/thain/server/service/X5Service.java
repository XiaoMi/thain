/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service;

import com.xiaomi.thain.server.entity.X5Config;
import lombok.NonNull;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-7 上午11:40
 */
public interface X5Service {

    X5Config getX5Config(@NonNull String appid);

}

