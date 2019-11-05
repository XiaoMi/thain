/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.process.service;

import com.xiaomi.thain.core.dao.UserDao;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Date 19-5-21 下午1:24
 *
 * @author liangyongrui@xiaomi.com
 */
@Log4j2
public class MailService {

    @NonNull
    private final String sender;
    @NonNull
    private final String senderUsername;
    @NonNull
    private final String senderPassword;

    @NonNull
    private final Properties props;

    @NonNull
    private final UserDao userDao;

    private MailService(@NonNull String host, @NonNull String sender,
                        @NonNull String senderUsername, @NonNull String senderPassword, @NonNull UserDao userDao) {
        this.sender = sender;
        this.senderUsername = senderUsername;
        this.senderPassword = senderPassword;
        this.userDao = userDao;
        props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", host);
    }

    public static MailService getInstance(@NonNull String host, @NonNull String sender, @NonNull String senderUsername,
                                          @NonNull String senderPassword, @NonNull UserDao userDao) {
        return new MailService(host, sender, senderUsername, senderPassword, userDao);
    }

    /**
     * 发送邮件
     *
     * @param to 邮件发送给to
     * @param subject 邮件主题
     * @param content 邮件内容（支持html）
     * @param attachments 附件
     * @throws MessagingException if multipart creation failed
     */
    public void send(@NonNull String[] to, @NonNull String subject, @NonNull String content, @NonNull Map<String, InputStream> attachments)
            throws MessagingException, IOException {
        Session session = Session.getInstance(props);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(sender));
        for (String t : to) {
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(t));
        }
        msg.setSubject(subject, "UTF-8");
        MimeMultipart mm = new MimeMultipart();
        MimeBodyPart contentMbp = new MimeBodyPart();
        contentMbp.setContent(content, "text/html;charset=UTF-8");
        mm.addBodyPart(contentMbp);
        if (!attachments.isEmpty()) {
            for (Map.Entry<String, InputStream> attachment : attachments.entrySet()) {
                MimeBodyPart mbp = new MimeBodyPart();
                ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(attachment.getValue(), "application/octet-stream");
                byteArrayDataSource.setName(attachment.getKey());
                mbp.setDataHandler(new DataHandler(byteArrayDataSource));
                mbp.setFileName(MimeUtility.encodeText(attachment.getKey()));
                mm.addBodyPart(mbp);
            }
        }
        mm.setSubType("mixed");
        msg.setContent(mm);
        Transport transport = session.getTransport();
        transport.connect(senderUsername, senderPassword);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    public void send(@NonNull String[] to, @NonNull String subject, @NonNull String content) throws IOException, MessagingException {
        send(to, subject, content, Collections.emptyMap());
    }

    public void send(@NonNull String to, @NonNull String subject, @NonNull String content) throws IOException, MessagingException {
        send(new String[] {to}, subject, content);
    }

    public void sendSeriousError(@NonNull String s) {
        try {
            val emails = userDao.getAdminUsers().stream().map(t -> t.email).filter(StringUtils::isNotBlank).toArray(String[]::new);
            if (emails.length > 0) {
                send(emails, "Thain serious error", InetAddress.getLocalHost().toString() + ":\n" + s);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
