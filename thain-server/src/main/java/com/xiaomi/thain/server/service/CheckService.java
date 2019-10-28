/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.service;

import com.xiaomi.thain.common.exception.ThainException;
import com.xiaomi.thain.common.model.FlowModel;
import com.xiaomi.thain.common.model.JobModel;
import lombok.NonNull;

import java.util.List;

/**
 * Date 19-5-27 下午5:55
 *
 * @author liangyongrui@xiaomi.com
 */
public interface CheckService {

    /**
     * 检查flowModel是否合法，不合法则抛出异常
     *
     * @param flowModel flowModel
     * @throws ThainException 不合法的异常
     */
    void checkFlowModel(@NonNull FlowModel flowModel) throws ThainException;

    void checkJobModelList(@NonNull List<JobModel> jobModelList) throws ThainException;
}
