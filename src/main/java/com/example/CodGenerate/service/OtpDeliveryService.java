package com.example.CodGenerate.service;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import com.example.CodGenerate.delivery.OtpDeliveryChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OtpDeliveryService {

  private final Map<String, OtpDeliveryChannel> channelMap;

  @Autowired
  public OtpDeliveryService(List<OtpDeliveryChannel> channels) {
    this.channelMap = channels.stream()
            .collect(Collectors.toMap(
                    OtpDeliveryChannel::getChannelName,
                    Function.identity()
            ));
  }

  public boolean send(OtpCodeEntity otpCode, String channel, String destination) {
    OtpDeliveryChannel deliveryChannel = channelMap.get(channel.toLowerCase());
    if (deliveryChannel == null) {
      throw new IllegalArgumentException("Unknown channel: " + channel);
    }
    return deliveryChannel.send(otpCode, destination);
  }
}