/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.common.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

/**
 * @author liangyongrui
 */
@ToString
public class X5 {
    private String body = "";
    private Header header = new Header();

    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String value) {
        this.body = value;
    }

    @JsonProperty("header")
    public Header getHeader() {
        return header;
    }

    @JsonProperty("header")
    public void setHeader(Header value) {
        this.header = value;
    }

    @ToString
    public class Header {
        private String appid = "";
        private String method = "";
        private String sign = "";
        private String url = "";

        @JsonProperty("appid")
        public String getAppid() {
            return appid;
        }

        @JsonProperty("appid")
        public void setAppid(String value) {
            this.appid = value;
        }

        @JsonProperty("method")
        public String getMethod() {
            return method;
        }

        @JsonProperty("method")
        public void setMethod(String value) {
            this.method = value;
        }

        @JsonProperty("sign")
        public String getSign() {
            return sign;
        }

        @JsonProperty("sign")
        public void setSign(String value) {
            this.sign = value;
        }

        @JsonProperty("url")
        public String getURL() {
            return url;
        }

        @JsonProperty("url")
        public void setURL(String value) {
            this.url = value;
        }
    }

}
