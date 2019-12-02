/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.common.entity;

import lombok.Data;

/**
 * @author liangyongrui
 */
@Data
public class X5Response {
    private Header header = new Header();
    private Object body = "";

    public X5Response setCode(String code) {
        this.header.code = code;
        return this;
    }

    public X5Response setDesc(String desc) {
        this.header.desc = desc;
        return this;
    }

    public X5Response setBody(Object body) {
        this.body = body;
        return this;
    }

    @Data
    public class Header {
        private Header() {
        }

        String code = "";
        String desc = "";
    }
}

