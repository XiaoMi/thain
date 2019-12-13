/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.server.mapper;

import com.xiaomi.thain.core.model.dr.JobDr;
import lombok.NonNull;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author liangyongrui@xiaomi.com
 */
@Component
public interface JobMapper {

    /**
     * 通过 flow id 和 name 和 deleted = 0 获取Job
     */
    Optional<JobDr> getJobByFlowIdAndName(@Param("flowId") long flowId, @Param("name") @NonNull String name);

    /**
     * 更新job的properties
     */
    void updateJobProperties(@Param("id") long id, @Param("properties") @NonNull String properties);
}
