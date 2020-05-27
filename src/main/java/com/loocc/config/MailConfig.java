package com.loocc.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件配置类
 */
@Configuration
public class MailConfig {
    //@Value("${spring.mail.port}")
    //private int port;
    //@Value("${spring.mail.host}")
    //private String host;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${email.fromAddr}")
    private String emailFrom;
    //
    //@Bean
    //public JavaMailSender mailSender(){
    //    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    //        mailSender.setHost(host);//指定用来发送邮件的服务器主机名
    //    mailSender.setPort(port);  //默认端口
    //    mailSender.setUsername(username);
    //    mailSender.setPassword(password);//设置授权码
    //    return mailSender;
    //}

    /***
     * 上面是用qq邮箱发送，但是有时候有问题
     * @return
     */

    @Bean("mailSession")
    public Session session() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.163.com");
        props.put("mail.smtp.port", "465");//
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        Session session = null;
        return session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username,
                                password);
                    }
                });
    }

    @Bean
    public MimeMessage mimeMessage(@Qualifier("mailSession") Session session){
        MimeMessage mimeMessage = new  MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(emailFrom));
            return mimeMessage;
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
