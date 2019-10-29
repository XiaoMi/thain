/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.model;

import lombok.Data;

import java.util.Date;
import javax.annotation.Nullable;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-7 上午11:36
 */
@Data
public class X5Config {
    @Nullable
    private Integer id;
    @Nullable
    private String appId;
    @Nullable
    private String appKey;
    @Nullable
    private Date createTime;
}

