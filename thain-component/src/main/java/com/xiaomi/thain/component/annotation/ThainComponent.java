/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.component.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Thain组件
 *
 * @author liangyongrui@xiaomi.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
@Inherited
public @interface ThainComponent {

    String value();

}
