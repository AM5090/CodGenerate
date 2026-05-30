package com.example.CodGenerate.service;

import com.example.CodGenerate.dao.repository.OtpCodeRepository;
import com.example.CodGenerate.dao.type.OtpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OtpExpirationScheduler {

  private static final Logger log = LoggerFactory.getLogger(OtpExpirationScheduler.class);

  @Autowired
  private OtpCodeRepository otpCodeRepository;

  @Scheduled(fixedDelay = 300000)
  @Transactional
  public void expireOldOtps() {
    log.info("Running scheduled task: expiring old OTP codes");

    LocalDateTime now = LocalDateTime.now();

    int updatedCount = otpCodeRepository.expireOldCodes(
            OtpStatus.EXPIRED,
            OtpStatus.ACTIVE,
            now
    );

    if (updatedCount > 0) {
      log.info("Expired {} OTP codes", updatedCount);
    } else {
      log.debug("No expired OTP codes found");
    }
  }
}