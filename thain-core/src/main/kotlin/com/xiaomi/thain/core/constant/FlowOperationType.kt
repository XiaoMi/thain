package com.xiaomi.thain.core.constant

enum class FlowOperationType(val code: Int) {
    /**
     * 创建
     */
    CREATE(1),
    /**
     * 修改
     */
    UPDATE(2),
    /**
     *  删除
     */
    DELETE(3),
    /**
     * 手动触发
     */
    MANUAL_TRIGGER(4),
    /**
     * 自动触发
     */
    AUTO_TRIGGER(5),
    /**
     * 重试触发
     */
    RETRY_TRIGGER(6),
    /**
     * 手动暂停
     */
    MANUAL_PAUSE(7),
    /**
     * 连续失败自动暂停
     */
    AUTO_PAUSE(8),
    /**
     * 开始
     */
    SCHEDULE(9),
    /**
     * 手动杀死
     */
    MANUAL_KILL(10),
    /**
     * 超时自动杀死
     */
    AUTO_KILL(11)
}

