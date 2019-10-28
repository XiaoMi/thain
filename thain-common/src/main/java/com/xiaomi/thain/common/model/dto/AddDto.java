/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model.dto;

import com.google.common.collect.ImmutableList;
import com.xiaomi.thain.common.model.FlowModel;
import com.xiaomi.thain.common.model.JobModel;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

/**
 * Date 19-7-9 下午8:14
 *
 * @author liangyongrui@xiaomi.com
 */
@Builder(toBuilder = true)
public class AddDto {
    @NonNull
    public final FlowModel flowModel;
    @NonNull
    public final List<JobModel> jobModelList;

    public AddDto(@NonNull FlowModel flowModel, @NonNull List<JobModel> jobModelList) {
        this.flowModel = flowModel;
        this.jobModelList = ImmutableList.copyOf(jobModelList);
    }
}
