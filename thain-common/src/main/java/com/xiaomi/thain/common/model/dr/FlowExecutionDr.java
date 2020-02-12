/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model.dr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.sql.Timestamp;

/**
 * flowExecution dr
 *
 * @author liangyongrui@xiaomi.com
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class FlowExecutionDr {
    /**
     * 自增id
     */
    public final long id;
    /**
     * 所属的流程id
     */
    public final long flowId;
    /**
     * 流程执行状态, 0 等待运行 1 执行中、2 执行结束、3 执行异常
     */
    public final int status;
    /**
     * 执行机器
     */
    @NonNull
    public final String hostInfo;
    /**
     * 触发类型，1手动，2自动调度
     */
    public final int triggerType;
    /**
     * 执行变量
     */
    public final String variables;
    /**
     * 流程执行日志
     */
    @Nullable
    public final String logs;
    /**
     * 创建时间
     */
    @NonNull
    public final Timestamp createTime;
    /**
     * 更新时间
     */
    @NonNull
    public final Timestamp updateTime;
    /**
     * 最近一次心跳时间
     */
    @NonNull
    public final Timestamp heartbeat;
}
