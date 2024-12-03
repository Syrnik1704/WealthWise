package com.example.wealthwise_api.config;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class EmailConfig {
    private final String email;
    private final String password;
    private Authenticator authenticator;
    private Session session;
    private Properties properties;

    public EmailConfig(@Value("${notification.email}") String email, @Value("${notification.password}") String password) {
        this.email = email;
        this.password = password;
        config();
    }

    private void config() {
        String smtpHost = "smtp.gmail.com";
        int smtpPort = 587;

        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);

        this.authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        };
    }

    private void refreshSession() {
        session = Session.getDefaultInstance(properties, authenticator);
    }

    public void sendEmail(String recipientEmail, String subject, String body, boolean onCreate) {
        if (session == null) {
            refreshSession();
        }
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(body, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException e) {
            if (onCreate) {
                refreshSession();
                sendEmail(recipientEmail, subject, body, false);
            }
        }
    }
}