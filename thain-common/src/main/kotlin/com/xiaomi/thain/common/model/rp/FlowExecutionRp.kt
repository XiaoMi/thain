package com.xiaomi.thain.common.model.rp

import com.alibaba.fastjson.JSON
import com.xiaomi.thain.common.model.dr.FlowExecutionDr
import java.sql.Timestamp

/**
 * flowExecution dr
 *
 * @author liangyongrui@xiaomi.com
 */
data class FlowExecutionRp(
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
        val variables: Map<String, Any>,
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
) {
    constructor(o: FlowExecutionDr) : this(
            id = o.id,
            flowId = o.flowId,
            status = o.status,
            hostInfo = o.hostInfo,
            triggerType = o.triggerType,
            variables = JSON.parseObject(o.variables),
            logs = o.logs,
            createTime = o.createTime,
            updateTime = o.updateTime,
            heartbeat = o.heartbeat
    )

}
