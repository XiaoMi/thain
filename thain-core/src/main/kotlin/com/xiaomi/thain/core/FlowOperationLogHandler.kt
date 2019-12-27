/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core

import com.xiaomi.thain.core.constant.FlowOperationType
import com.xiaomi.thain.core.mapper.FlowOperationLogMapper
import com.xiaomi.thain.core.model.dp.AddFlowOperationLogDp
import org.apache.ibatis.session.SqlSessionFactory

/**
 * 记录一切flow操作
 *
 * 创建, 修改, 删除, 手动触发, 暂停, 开始, 杀死
 * @author liangyongrui@xiaomi.com
 */
class FlowOperationLogHandler(
        val flowId: Long,
        /**
         * 操作类型
         */
        val operationType: FlowOperationType,
        /**
         * 操作来源 appid
         */
        val appId: String,
        /**
         * 操作用户
         */
        val username: String,
        /**
         * 其他信息
         */
        val extraInfo: String
) {

    fun save() {
        sqlSessionFactory!!.openSession().use { sqlSession ->
            sqlSession.getMapper(FlowOperationLogMapper::class.java)
                    .addLog(AddFlowOperationLogDp(
                            flowId = flowId,
                            operationType = operationType.code,
                            appId = appId,
                            username = username,
                            extraInfo = extraInfo
                    ))
            sqlSession.commit()
        }
    }

    companion object {
        private var sqlSessionFactory: SqlSessionFactory? = null

        fun setSqlSessionFactory(sqlSessionFactory: SqlSessionFactory) {
            this.sqlSessionFactory = sqlSessionFactory
        }
    }
}
