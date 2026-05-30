package com.example.CodGenerate.delivery;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class EmailService {

  private static final Logger log = LoggerFactory.getLogger(EmailService.class);
  private final String username;
  private final String password;
  private final String fromEmail;
  private final Session session;

  public EmailService() {
    Properties config = loadConfig();
    this.username = config.getProperty("email.username");
    this.password = config.getProperty("email.password");
    this.fromEmail = config.getProperty("email.from");

    Properties mailProps = new Properties();
    mailProps.put("mail.smtp.host", config.getProperty("mail.smtp.host"));
    mailProps.put("mail.smtp.port", config.getProperty("mail.smtp.port"));
    mailProps.put("mail.smtp.auth", config.getProperty("mail.smtp.auth"));
    mailProps.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable"));

    this.session = Session.getInstance(mailProps, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });
  }

  private Properties loadConfig() {
    try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
      Properties props = new Properties();
      props.load(input);
      return props;
    } catch (IOException e) {
      log.error("Failed to load email configuration", e);
      throw new RuntimeException("Failed to load email configuration", e);
    }
  }

  public void sendCode(String toEmail, String code) {
    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(fromEmail));
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
      message.setSubject("Your OTP Code");
      message.setText("Your verification code is: " + code);

      Transport.send(message);
      log.info("Email sent successfully to: {}", toEmail);
    } catch (MessagingException e) {
      log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
      throw new RuntimeException("Failed to send email", e);
    }
  }
}