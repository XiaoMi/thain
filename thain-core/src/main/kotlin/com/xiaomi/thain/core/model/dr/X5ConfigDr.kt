/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.model.dr

import java.sql.Timestamp

/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/29
 */
class X5ConfigDr(
        val id: Long,
        val appId: String,
        val appKey: String,
        val appName: String,
        val principal: String,
        val appDescription: String?,
        val createTime: Timestamp
)
