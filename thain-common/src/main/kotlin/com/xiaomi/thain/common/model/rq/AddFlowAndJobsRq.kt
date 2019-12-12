package com.xiaomi.thain.common.model.rq

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
