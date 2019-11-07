/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model.rq;

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
public class AddRq {
    @NonNull
    public final AddFlowRq addFlowRq;
    @NonNull
    public final List<JobModel> jobModelList;

    public AddRq(@NonNull AddFlowRq addFlowRq, @NonNull List<JobModel> jobModelList) {
        this.addFlowRq = addFlowRq;
        this.jobModelList = ImmutableList.copyOf(jobModelList);
    }
}
