package com.example.CodGenerate.delivery;

import org.jsmpp.bean.*;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SubmitSmResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
public class SmsService {

  private static final Logger log = LoggerFactory.getLogger(SmsService.class);
  private final String host;
  private final int port;
  private final String systemId;
  private final String password;
  private final String systemType;
  private final String sourceAddress;

  public SmsService() {
    Properties config = loadConfig();
    this.host = config.getProperty("smpp.host");
    this.port = Integer.parseInt(config.getProperty("smpp.port"));
    this.systemId = config.getProperty("smpp.system_id");
    this.password = config.getProperty("smpp.password");
    this.systemType = config.getProperty("smpp.system_type");
    this.sourceAddress = config.getProperty("smpp.source_addr");
  }

  private Properties loadConfig() {
    try (InputStream input = getClass().getClassLoader().getResourceAsStream("sms.properties")) {
      Properties props = new Properties();
      props.load(input);
      return props;
    } catch (IOException e) {
      log.error("Failed to load SMS configuration", e);
      throw new RuntimeException("Failed to load SMS configuration", e);
    }
  }

  public void sendCode(String phoneNumber, String code) {
    SMPPSession session = new SMPPSession();
    try {
      BindParameter bindParameter = new BindParameter(
              BindType.BIND_TX,
              systemId,
              password,
              systemType,
              TypeOfNumber.UNKNOWN,
              NumberingPlanIndicator.UNKNOWN,
              sourceAddress
      );

      session.connectAndBind(host, port, bindParameter);
      log.debug("Connected to SMPP server at {}:{}", host, port);

      String messageText = "Your verification code is: " + code;


      SubmitSmResult result = session.submitShortMessage(
              systemType,
              TypeOfNumber.UNKNOWN,
              NumberingPlanIndicator.UNKNOWN,
              sourceAddress,
              TypeOfNumber.UNKNOWN,
              NumberingPlanIndicator.UNKNOWN,
              phoneNumber,
              new ESMClass(),
              (byte) 0,
              (byte) 1,
              null,
              null,
              new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
              (byte) 0,
              new GeneralDataCoding(Alphabet.ALPHA_DEFAULT),
              (byte) 0,
              messageText.getBytes(StandardCharsets.UTF_8)
      );

      String messageId = result.getMessageId();
      log.info("SMS sent successfully to {}: messageId={}", phoneNumber, messageId);

    } catch (Exception e) {
      log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
      throw new RuntimeException("Failed to send SMS", e);
    } finally {
      if (session != null && session.getSessionState().isBound()) {
        session.unbindAndClose();
        log.debug("SMPP session closed");
      }
    }
  }
}