/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process.runtime.notice;

import com.xiaomi.thain.core.process.service.MailService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

/**
 * Date 19-5-21 下午12:46
 *
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MailNotice {

    @NonNull
    private MailService mailService;
    @NonNull
    private String callbackEmail;

    public static MailNotice getInstance(@NonNull MailService mailService, @NonNull String callbackEmail) {
        return new MailNotice(mailService, callbackEmail);
    }

    /**
     * 发送错误通知
     */
    public void sendError(@NonNull String errorMessage) {
        if (Strings.isBlank(callbackEmail)) {
            return;
        }
        try {
            mailService.send(callbackEmail, "Thain flow executed failed", errorMessage);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
