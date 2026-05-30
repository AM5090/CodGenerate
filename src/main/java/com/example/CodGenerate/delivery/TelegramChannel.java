package com.example.CodGenerate.delivery;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TelegramChannel implements OtpDeliveryChannel {

  private static final Logger log = LoggerFactory.getLogger(TelegramChannel.class);

  @Autowired
  private TelegramService telegramService;

  @Override
  public boolean send(OtpCodeEntity otpCode, String username) {
    try {
      telegramService.sendCode(username, otpCode.getCode());
      log.info("OTP отправлен на телеграмм пользователю: {}", username);
      return true;
    } catch (Exception e) {
      log.error("Не удалось отправить OTP через Telegram на {}: {}", username, e.getMessage());
      return false;
    }
  }

  @Override
  public String getChannelName() {
    return "telegram";
  }
}