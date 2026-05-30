package com.example.CodGenerate.service;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import com.example.CodGenerate.dao.entity.OtpConfigEntity;
import com.example.CodGenerate.dao.entity.UserEntity;
import com.example.CodGenerate.dao.repository.OtpCodeRepository;
import com.example.CodGenerate.dao.repository.OtpConfigRepository;
import com.example.CodGenerate.dao.type.OtpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

  private static final Logger log = LoggerFactory.getLogger(OtpService.class);

  @Autowired
  private OtpCodeRepository otpCodeRepository;

  @Autowired
  private OtpConfigRepository otpConfigRepository;

  @Autowired
  private UserService userService;

  private final SecureRandom random = new SecureRandom();

  @Transactional
  public OtpCodeEntity generateOtp(String login, String operationId) {
    log.info("Generating OTP for user: {}, operationId: {}", login, operationId);

    UserEntity user = userService.findByLogin(login)
            .orElseThrow(() -> {
              log.error("User not found for OTP generation: {}", login);
              return new RuntimeException("User not found");
            });

    OtpConfigEntity config = otpConfigRepository.getSingleConfig();

    String code = generateNumericCode(config.getCodeLength());

    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(config.getCodeExpiryMinutes());

    OtpCodeEntity otpCode = new OtpCodeEntity(
            user,
            operationId,
            code,
            expiresAt,
            config.getMaxAttempts()
    );

    OtpCodeEntity saved = otpCodeRepository.save(otpCode);

    log.info("OTP generated successfully for user: {}, operationId: {}, expiresAt: {}",
            login, operationId, expiresAt);

    return saved;
  }

  @Transactional
  public boolean validateOtp(String operationId, String code, String ipAddress) {
    log.info("Validating OTP for operationId: {}, ipAddress: {}", operationId, ipAddress);

    Optional<OtpCodeEntity> otpOpt = otpCodeRepository.findByOperationIdAndStatus(
            operationId, OtpStatus.ACTIVE
    );

    if (otpOpt.isEmpty()) {
      log.warn("No active OTP found for operationId: {}", operationId);
      return false;
    }

    OtpCodeEntity otpCode = otpOpt.get();

    if (otpCode.getExpiresAt().isBefore(LocalDateTime.now())) {
      otpCode.setStatus(OtpStatus.EXPIRED);
      otpCodeRepository.save(otpCode);
      log.warn("OTP expired for operationId: {}, expiresAt: {}", operationId, otpCode.getExpiresAt());
      return false;
    }

    if (otpCode.getAttemptsLeft() <= 0) {
      otpCode.setStatus(OtpStatus.EXPIRED);
      otpCodeRepository.save(otpCode);
      log.warn("OTP attempts exhausted for operationId: {}", operationId);
      return false;
    }

    if (!otpCode.getCode().equals(code)) {
      otpCode.setAttemptsLeft(otpCode.getAttemptsLeft() - 1);
      otpCodeRepository.save(otpCode);
      log.warn("Invalid OTP code for operationId: {}, attempts left: {}", operationId, otpCode.getAttemptsLeft());
      return false;
    }

    otpCode.setStatus(OtpStatus.USED);
    otpCode.setConfirmedAt(LocalDateTime.now());
    otpCode.setConfirmedByIp(ipAddress);
    otpCodeRepository.save(otpCode);

    log.info("OTP validated successfully for operationId: {}", operationId);
    return true;
  }

  public OtpStatus getOtpStatus(String operationId) {
    return otpCodeRepository.findByOperationIdAndStatus(operationId, OtpStatus.ACTIVE)
            .map(OtpCodeEntity::getStatus)
            .orElse(null);
  }

  private String generateNumericCode(int length) {
    StringBuilder code = new StringBuilder();
    for (int i = 0; i < length; i++) {
      code.append(random.nextInt(10));
    }
    return code.toString();
  }

  public OtpConfigEntity getCurrentConfig() {
    return otpConfigRepository.getSingleConfig();
  }

  @Transactional
  public OtpConfigEntity updateConfig(Integer codeLength, Integer codeExpiryMinutes, Integer maxAttempts) {
    log.info("Updating OTP config: length={}, expiryMinutes={}, maxAttempts={}", codeLength, codeExpiryMinutes, maxAttempts);

    OtpConfigEntity config = otpConfigRepository.getSingleConfig();

    if (codeLength != null && codeLength >= 4 && codeLength <= 10) {
      config.setCodeLength(codeLength);
    }
    if (codeExpiryMinutes != null && codeExpiryMinutes >= 1 && codeExpiryMinutes <= 30) {
      config.setCodeExpiryMinutes(codeExpiryMinutes);
    }
    if (maxAttempts != null && maxAttempts >= 1 && maxAttempts <= 10) {
      config.setMaxAttempts(maxAttempts);
    }

    OtpConfigEntity saved = otpConfigRepository.save(config);
    log.info("OTP config updated successfully");

    return saved;
  }
}