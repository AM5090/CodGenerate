package com.example.CodGenerate.delivery;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;

public interface OtpDeliveryChannel {
  boolean send(OtpCodeEntity otpCode, String destination);
  String getChannelName();
}