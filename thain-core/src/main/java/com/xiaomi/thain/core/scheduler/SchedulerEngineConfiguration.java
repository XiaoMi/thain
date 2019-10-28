/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.scheduler;

import lombok.NonNull;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author liangyongrui@xiaomi.com
 * @date 19-5-16 下午8:35
 */
public class SchedulerEngineConfiguration {

    @NonNull
    public final Properties properties;

    private SchedulerEngineConfiguration(@NonNull Properties properties) {
        this.properties = properties;
    }

    public static SchedulerEngineConfiguration getInstanceByInputStream(@NonNull InputStream in) throws IOException {
        val properties = new Properties();
        properties.load(in);
        return new SchedulerEngineConfiguration(properties);
    }
}
