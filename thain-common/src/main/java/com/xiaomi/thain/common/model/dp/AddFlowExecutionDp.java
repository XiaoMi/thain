/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model.dp;

import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nullable;

/**
 * flowExecution dp
 *
 * @author liangyongrui@xiaomi.com
 */
@Builder
public class AddFlowExecutionDp {
    /**
     * 自增id
     */
    @Nullable
    public final Long id;
    /**
     * 所属的流程id
     */
    @NonNull
    public final Long flowId;
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
}
