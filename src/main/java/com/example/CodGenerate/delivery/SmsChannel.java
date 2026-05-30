package com.example.CodGenerate.delivery;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsChannel implements OtpDeliveryChannel {

  private static final Logger log = LoggerFactory.getLogger(SmsChannel.class);

  @Autowired
  private SmsService smsService;

  @Override
  public boolean send(OtpCodeEntity otpCode, String phoneNumber) {
    try {
      smsService.sendCode(phoneNumber, otpCode.getCode());
      log.info("OTP отправлен по SMS на: {}", phoneNumber);
      return true;
    } catch (Exception e) {
      log.error("Не удалось отправить OTP по SMS на {}: {}", phoneNumber, e.getMessage());
      return false;
    }
  }

  @Override
  public String getChannelName() {
    return "sms";
  }
}