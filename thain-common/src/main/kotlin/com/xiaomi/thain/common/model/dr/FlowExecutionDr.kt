package com.xiaomi.thain.common.model.dr

import java.sql.Timestamp

/**
 * flowExecution dr
 *
 * @author liangyongrui@xiaomi.com
 */
data class FlowExecutionDr(
        /**
         * 自增id
         */
        val id: Long,
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
        val hostInfo: String?,
        /**
         * 触发类型，1手动，2自动调度
         */
        val triggerType: Int,
        /**
         * 执行变量
         */
        val variables: String?,
        /**
         * 流程执行日志
         */
        val logs: String?,
        /**
         * 创建时间
         */
        val createTime: Timestamp?,
        /**
         * 更新时间
         */
        val updateTime: Timestamp?,
        /**
         * 最近一次心跳时间
         */
        val heartbeat: Timestamp?
)
