/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.sql.Timestamp;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Date 19-5-20 下午9:00
 *
 * @author liangyongrui@xiaomi.com
 */
@Builder
@AllArgsConstructor
public class JobExecutionModel {
    public final long id;
    public final long flowExecutionId;
    public final long jobId;
    public final int status;
    @Nullable
    public final String logs;
    @Nullable
    public final Timestamp createTime;
    @Nullable
    public final Timestamp updateTime;

    public Optional<Long> getCreateTime() {
        return Optional.ofNullable(createTime).map(Timestamp::getTime);
    }

    public Optional<Long> getUpdateTime() {
        return Optional.ofNullable(updateTime).map(Timestamp::getTime);
    }
}
