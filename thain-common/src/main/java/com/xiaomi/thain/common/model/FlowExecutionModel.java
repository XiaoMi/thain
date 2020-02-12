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
 * flowExecution model
 *
 * @author liangyongrui@xiaomi.com
 */
@AllArgsConstructor
@Builder
@Deprecated
public class FlowExecutionModel {
    /**
     * 自增id
     */
    public final long id;
    /**
     * 所属的流程id
     */
    public final long flowId;
    /**
     * 流程执行状态,1 执行中、2 执行结束、3 执行异常
     */
    public final int status;
    /**
     * 执行机器
     */
    @Nullable
    public final String hostInfo;
    /**
     * 触发类型，1手动，2自动调度
     */
    public final int triggerType;
    /**
     * 流程执行日志
     */
    @Nullable
    public final String logs;
    /**
     * 创建时间
     */
    @Nullable
    public final Timestamp createTime;
    /**
     * 更新时间
     */
    @Nullable
    public final Timestamp updateTime;

    public Optional<Long> getCreateTime() {
        return Optional.ofNullable(createTime).map(Timestamp::getTime);
    }

    public Optional<Long> getUpdateTime() {
        return Optional.ofNullable(updateTime).map(Timestamp::getTime);
    }
}
