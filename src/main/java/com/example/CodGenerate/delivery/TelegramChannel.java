package com.example.CodGenerate.delivery;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TelegramChannel implements OtpDeliveryChannel {

  private static final Logger log = LoggerFactory.getLogger(TelegramChannel.class);

  @Override
  public boolean send(OtpCodeEntity otpCode, String chatId) {
    log.info("[Telegram] Отправка кода {} в чат {}", otpCode.getCode(), chatId);
    log.debug("OTP код: {}, истекает через: {}", otpCode.getCode(), otpCode.getExpiresAt());

    System.out.println("=== TELEGRAM ===");
    System.out.println("Chat ID: " + chatId);
    System.out.println("Код: " + otpCode.getCode());
    System.out.println("Действителен до: " + otpCode.getExpiresAt());
    System.out.println("================");

    log.info("OTP успешно отправлен в телеграмм на: {}", chatId);
    return true;
  }

  @Override
  public String getChannelName() {
    return "telegram";
  }
}