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

//    /**
//     * 组件所属的组，如：std，用于区分组件
//     *
//     * @return 组件所属组的名称
//     */
//    String group();
//
//    /**
//     * 组件名称， 如：http
//     *
//     * @return 组件的名称
//     */
//    String name();
//
//    /**
//     * 组件定义的json
//     * http://json-schema.org/draft-07/schema
//     * 这个json用来字段校验，前端组件展示等
//     *
//     * @return 组件定义的json
//     */
//    String defineJson();

    //todo 上面的都要删除
    String value();
}
