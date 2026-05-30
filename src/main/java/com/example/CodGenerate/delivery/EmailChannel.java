package com.example.CodGenerate.delivery;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailChannel implements OtpDeliveryChannel {

  private static final Logger log = LoggerFactory.getLogger(EmailChannel.class);

  @Autowired
  private EmailService emailService;

  @Override
  public boolean send(OtpCodeEntity otpCode, String destination) {
    try {
      emailService.sendCode(destination, otpCode.getCode());
      log.info("OTP отправлен по email на адрес: {}", destination);
      return true;
    } catch (Exception e) {
      log.error("Не удалось отправить OTP по электронной почте на {}: {}", destination, e.getMessage());
      return false;
    }
  }

  @Override
  public String getChannelName() {
    return "email";
  }
}