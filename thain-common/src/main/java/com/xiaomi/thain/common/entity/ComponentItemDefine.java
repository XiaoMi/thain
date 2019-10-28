/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.entity;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.List;

/**
 * Date 19-5-27 下午5:28
 *
 * @author liangyongrui@xiaomi.com
 */
@AllArgsConstructor
public class ComponentItemDefine {

    @AllArgsConstructor
    public static class Option {
        @NonNull
        public final String id;
        @NonNull
        public final String name;
    }

    @AllArgsConstructor
    public static class Input {
        @NonNull
        public final String id;
        @NonNull
        public final List<Option> options;
    }

    @NonNull
    public final String property;
    public final boolean required;
    @NonNull
    public final String label;
    @NonNull
    public final Input input;
}

