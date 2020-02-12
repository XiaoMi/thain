package com.xiaomi.thain.core.process.service

import com.xiaomi.thain.core.dao.UserDao
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.net.InetAddress
import java.util.*
import javax.activation.DataHandler
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.*
import javax.mail.util.ByteArrayDataSource

/**
 * Date 19-5-21 下午1:24
 *
 * @author liangyongrui@xiaomi.com
 */
class MailService private constructor(host: String,
                                      private val sender: String,
                                      private val senderUsername: String,
                                      private val senderPassword: String,
                                      private val userDao: UserDao) {
    private val props: Properties = Properties()

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    /**
     * 发送邮件
     *
     * @param to 邮件发送给to
     * @param subject 邮件主题
     * @param content 邮件内容（支持html）
     * @param attachments 附件
     * @throws MessagingException if multipart creation failed
     */
    @JvmOverloads
    @Throws(MessagingException::class, IOException::class)
    fun send(to: List<String>, subject: String, content: String, attachments: Map<String, InputStream> = emptyMap()) {
        val session = Session.getInstance(props)
        val msg = MimeMessage(session)
        msg.setFrom(InternetAddress(sender))
        for (t in to) {
            msg.addRecipient(Message.RecipientType.TO, InternetAddress(t))
        }
        msg.setSubject(subject, "UTF-8")
        val mm = MimeMultipart()
        val contentMbp = MimeBodyPart()
        contentMbp.setContent(content, "text/html;charset=UTF-8")
        mm.addBodyPart(contentMbp)
        if (attachments.isNotEmpty()) {
            for ((key, value) in attachments) {
                val mbp = MimeBodyPart()
                val byteArrayDataSource = ByteArrayDataSource(value, "application/octet-stream")
                byteArrayDataSource.name = key
                mbp.dataHandler = DataHandler(byteArrayDataSource)
                mbp.fileName = MimeUtility.encodeText(key)
                mm.addBodyPart(mbp)
            }
        }
        mm.setSubType("mixed")
        msg.setContent(mm)
        val transport = session.transport
        transport.connect(senderUsername, senderPassword)
        transport.sendMessage(msg, msg.allRecipients)
        transport.close()
    }

    @Throws(IOException::class, MessagingException::class)
    fun send(to: String, subject: String, content: String) {
        send(listOf(to), subject, content)
    }

    fun sendSeriousError(s: String) {
        try {
            val emails = userDao.adminUsers.mapNotNull { it.email }.filter { it.isNotBlank() }
            if (emails.isNotEmpty()) {
                send(emails, "Thain serious error", "${InetAddress.getLocalHost()}:\n$s")
            }
        } catch (e: Exception) {
            log.error("", e)
        }
    }

    companion object {
        fun getInstance(host: String, sender: String, senderUsername: String,
                        senderPassword: String, userDao: UserDao): MailService {
            return MailService(host, sender, senderUsername, senderPassword, userDao)
        }
    }

    init {
        props.setProperty("mail.smtp.auth", "true")
        props.setProperty("mail.transport.protocol", "smtp")
        props.setProperty("mail.smtp.host", host)
    }
}
