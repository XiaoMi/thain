package com.xiaomi.thain.common.model.dp

/**
 * flowExecution dp
 *
 * @author liangyongrui@xiaomi.com
 */
data class AddFlowExecutionDp(
        /**
         * 所属的流程id
         */
        val flowId: Long,
        /**
         * 流程执行状态, 0 等待运行 1 执行中、2 执行结束、3 执行异常
         */
        val status: Int,
        /**
         * 执行机器
         */
        val hostInfo: String,
        /**
         * 触发类型，1手动，2自动调度
         */
        val triggerType: Int,
        val variables: String?) {
    /**
     * 自增id
     */
    val id: Long? = null

}
