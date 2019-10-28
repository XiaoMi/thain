/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */

package com.xiaomi.thain.component.std;

import com.xiaomi.thain.component.annotation.ThainComponent;
import com.xiaomi.thain.component.tools.ComponentTools;
import lombok.val;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Date 19-5-16 下午8:48
 *
 * @author liangyongrui@xiaomi.com
 */
@ThainComponent(group = "std", name = "mail",
        defineJson = "[{\"property\": \"title\", \"label\": \"邮件标题\", \"required\": true, \"input\": {\"id\": \"textarea\"}}, {\"property\": \"contentHtml\", \"label\": \"邮件内容\", \"required\": true, \"input\": {\"id\": \"richText\"}}, {\"property\": \"recipient\", \"label\": \"收件人（多个用逗号隔开）\", \"required\": true, \"input\": {\"id\": \"textarea\"}}]")
@SuppressWarnings("unused")
public class MailComponent {
    /**
     * 流程执行工具
     */
    private ComponentTools tools;
    /**
     * 邮件标题
     */
    private String title;

    /**
     * 邮件内容，html格式
     */
    private String contentHtml;

    /**
     * 邮件接收人，多个用逗号隔开
     */
    private String recipient;

    @SuppressWarnings("unused")
    private void run() throws IOException, MessagingException {

        tools.addDebugLog("title:" + title);
        tools.addDebugLog("email content:" + contentHtml);
        tools.addDebugLog("recipient:" + recipient);

        String[] to = recipient.split(",");

        val sb = new StringBuilder(contentHtml);
        for (int beginIndex = 0; beginIndex < sb.length(); beginIndex++) {
            int cur = sb.indexOf("${", beginIndex);
            if (cur == -1) {
                break;
            }
            int end = sb.indexOf("}", cur + 1);
            if (end == -1) {
                break;
            }
            val key = sb.substring(cur + 2, end).split("\\.");
            if (key.length == 2) {
                tools.getStorageValue(key[0], key[1])
                        .ifPresent(t -> sb.replace(cur, end + 1, String.valueOf(t)));

            }
        }
        tools.sendMail(to, title, sb.toString());
    }

}
