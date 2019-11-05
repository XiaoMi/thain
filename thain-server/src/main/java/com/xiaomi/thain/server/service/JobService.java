/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.service;

import com.xiaomi.thain.common.model.rq.UpdateJobPropertiesRq;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * @author liangyongrui
 */
@Service
public interface JobService {

    /**
     * 更新指定job 的properties
     * 如果key存在就更新，不存在就追加
     */
    void updateJobProperties(@NonNull UpdateJobPropertiesRq updateJobPropertiesRq);
}
