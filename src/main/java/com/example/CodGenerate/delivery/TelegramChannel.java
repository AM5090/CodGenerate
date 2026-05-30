package com.example.CodGenerate.delivery;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TelegramChannel implements OtpDeliveryChannel {

  private static final Logger logger = LoggerFactory.getLogger(TelegramChannel.class);

  @Override
  public boolean send(OtpCodeEntity otpCode, String chatId) {
    logger.info("[Telegram] Отправка кода {} в чат {}", otpCode.getCode(), chatId);
    System.out.println("=== TELEGRAM ===");
    System.out.println("Chat ID: " + chatId);
    System.out.println("Код: " + otpCode.getCode());
    System.out.println("Действителен до: " + otpCode.getExpiresAt());
    System.out.println("================");
    return true;
  }

  @Override
  public String getChannelName() {
    return "telegram";
  }
}