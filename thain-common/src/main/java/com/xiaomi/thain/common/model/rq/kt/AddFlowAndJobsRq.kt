/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.model.rq.kt


/**
 * Date 19-7-9 下午8:14
 *
 * @author liangyongrui@xiaomi.com
 */
data class AddFlowAndJobsRq(
        /**
         * 为了兼容前端和旧的sdk 所以叫flowModel 和 jobModelList
         */
        val flowModel: AddFlowRq,
        val jobModelList: List<AddJobRq>
)
