package com.example.CodGenerate.delivery;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailChannel implements OtpDeliveryChannel {

  private static final Logger logger = LoggerFactory.getLogger(EmailChannel.class);

  @Override
  public boolean send(OtpCodeEntity otpCode, String email) {
    logger.info("[Email] Отправка кода {} на email {}", otpCode.getCode(), email);
    System.out.println("=== EMAIL ===");
    System.out.println("Email: " + email);
    System.out.println("Код: " + otpCode.getCode());
    System.out.println("Действителен до: " + otpCode.getExpiresAt());
    System.out.println("=============");
    return true;
  }

  @Override
  public String getChannelName() {
    return "email";
  }
}