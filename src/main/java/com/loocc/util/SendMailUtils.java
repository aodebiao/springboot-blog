package com.loocc.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 本来想用这个类,但是没
 */
public class SendMailUtils {
    private static String from = "z582829957@163.com";
    private static String user = "z582829957@163.com";
    private static String password = "";

    public static void sendMail(String to,String text,String title) {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", "smtp.163.com");//设置邮件服务器主机名
        props.put("mail.smtp.host", "smtp.163.com");
        props.put("mail.smtp.auth", "true");//发送服务器需要身份验证
        Session session = Session.getDefaultInstance(props);//设置环境信息
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        Transport transport = null;
        //Multipart multipart = null;
        BodyPart contentPart = null;
        try {
            message.setFrom(new InternetAddress(from));//设置发件人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(title);
            //multipart = new MimeMultipart();//设置附件
            contentPart = new MimeBodyPart();
            contentPart.setContent(text, "text/html;charset=utf-8");
            //multipart.addBodyPart(contentPart);
            //message.setContent(multipart);
            message.saveChanges();
            transport = session.getTransport("smtp");
            transport.connect("smtp.163.com", user, password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {

            e.printStackTrace();
        }finally {
            try {
                transport.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        //sendMail("582829957@qq.com","哈哈哈哈","加油");
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.163.com");
            props.put("mail.smtp.port", "25");//
            props.put("mail.smtp.socketFactory.port", "port");
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("z582829957@163.com",
                                    "");

                        }

                    });
        //Session session = Session.getDefaultInstance(props);
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("z582829957@163.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse("582829957@qq.com"));
                message.setSubject("Testing Subject");
                message.setText("Dear User," + "\n\n This is testing only!");
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();

            }
    }

}
