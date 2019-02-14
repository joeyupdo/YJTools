package com.amused.joey.email;

import com.amused.joey.mainkit.MainThreadKit;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/31 09:28
 * Description: 发送邮件，
 */
public class YJEmail {
    private final Builder builder;

    private YJEmail(Builder builder) {
        this.builder = builder;
    }

    public void sendHtml(final String title, final String content, final String[] pathNames) {
        _send(title, new EmailContent(true, content), pathNames);
    }

    /**
     * @param title  邮件的标题
     * @param content 邮件的文本内容
     * @param pathNames  附件的路径列表，没有可填空
     */
    public void sendText(final String title, final String content, final String[] pathNames) {
        _send(title, new EmailContent(false, content), pathNames);
    }

    private void _send(final String title, final EmailContent content, final String[] pathNames) {
        try {
            final Message message = initMessage(title, content, pathNames);
            // 发送邮件
            if (MainThreadKit.isOnMain()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Transport.send(message);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                        synchronized (message) {
                            message.notifyAll();
                        }
                    }
                }).start();
                synchronized (message) {
                    message.wait();
                }
            } else {
                Transport.send(message);
            }
        } catch (MessagingException | IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private Properties getProperties() {
        //用于存放 SMTP 服务器地址等参数
        Properties properties = new Properties();
        // 主机地址
        properties.put("mail.smtp.host", builder.host);
        // 端口
        properties.put("mail.smtp.port", String.valueOf(builder.port));
        // 邮件协议
        properties.put("mail.transport.protocol", "smtp");
        // 认证
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback","false");
        properties.put("mail.smtp.starttls.enable", "true");
        return properties;
    }

    /**
     * 创建发送消息体
     * @param title
     * @param emailContent
     * @param pathNames
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    private Message initMessage(String title, EmailContent emailContent, String[] pathNames) throws IOException, MessagingException {
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Properties properties = getProperties();
        MailAuthenticator mailAuthenticator = new MailAuthenticator(builder.fromUser, builder.fromPassword);
        Session session = Session.getDefaultInstance(properties, mailAuthenticator);
        session.setDebug(builder.debug);
        // 根据session创建一个邮件消息
        Message mailMessage = new MimeMessage(session);
        // 设置邮件消息的发送者
        mailMessage.setFrom(new InternetAddress(builder.fromUser, builder.fromAlias));
        // 创建邮件的接收者地址，并设置到邮件消息中
        mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(builder.toUser, builder.toAlias));
        // 设置邮件消息的主题
        mailMessage.setSubject(title);
        // 设置邮件消息发送的时间
        mailMessage.setSentDate(new Date());
        // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
        Multipart multipart = new MimeMultipart();
        // 创建一个包含邮件内容的MimeBodyPart
        BodyPart contentPart = new MimeBodyPart();
        // 设置内容
        contentPart.setContent(emailContent.content, "text/" + (emailContent.isHtml? "html": "plain") + "; charset=utf-8");
        multipart.addBodyPart(contentPart);
        if (null != pathNames) {
            for (String pathName : pathNames) {
                MimeBodyPart attachment = new MimeBodyPart();
                attachment.attachFile(pathName);
                multipart.addBodyPart(attachment);
            }
        }
        mailMessage.setContent(multipart);
        return mailMessage;
    }

    public static class Builder {
        private String host;
        private int port;
        private String fromUser;
        private String fromPassword;
        private String fromAlias;
        private String toUser;
        private String toAlias;
        private boolean debug;

        public Builder() {
            debug = false;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setFromUser(String fromUser) {
            this.fromUser = fromUser;
            return this;
        }

        public Builder setFromPassword(String fromPassword) {
            this.fromPassword = fromPassword;
            return this;
        }

        public Builder setFromAlias(String fromAlias) {
            this.fromAlias = fromAlias;
            return this;
        }

        public Builder setToUser(String toUser) {
            this.toUser = toUser;
            return this;
        }

        public Builder setToAlias(String toAlias) {
            this.toAlias = toAlias;
            return this;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public YJEmail build() {
            if (null == host) throw new IllegalArgumentException("Input \"host\" can\'t be null!");
            if (null == fromUser) throw new IllegalArgumentException("Input \"fromUser\" can\'t be null!");
            if (null == fromPassword) throw new IllegalArgumentException("Input \"fromPassword\" can\'t be null!");
            if (null == toUser) throw new IllegalArgumentException("Input \"toUser\" can\'t be null!");
            if (null == fromAlias) {
                fromAlias = fromUser;
            }
            if (null == toAlias) {
                toAlias = toUser;
            }
            if (port <= 0) {
                port = 465;
            }
            return new YJEmail(this);
        }
    }
}
