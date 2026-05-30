package com.example.CodGenerate.delivery;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsEmulatorChannel implements OtpDeliveryChannel {

  private static final Logger logger = LoggerFactory.getLogger(SmsEmulatorChannel.class);

  @Override
  public boolean send(OtpCodeEntity otpCode, String phoneNumber) {
    logger.info("[SMS Emulator] Отправка кода {} на номер {}", otpCode.getCode(), phoneNumber);
    System.out.println("=== SMS ЭМУЛЯТОР ===");
    System.out.println("Номер: " + phoneNumber);
    System.out.println("Код: " + otpCode.getCode());
    System.out.println("Действителен до: " + otpCode.getExpiresAt());
    System.out.println("==================");
    return true;
  }

  @Override
  public String getChannelName() {
    return "sms";
  }
}