package com.example.CodGenerate.service;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import com.example.CodGenerate.dao.entity.OtpConfigEntity;
import com.example.CodGenerate.dao.entity.UserEntity;
import com.example.CodGenerate.dao.repository.OtpCodeRepository;
import com.example.CodGenerate.dao.repository.OtpConfigRepository;
import com.example.CodGenerate.dao.type.OtpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

  @Autowired
  private OtpCodeRepository otpCodeRepository;

  @Autowired
  private OtpConfigRepository otpConfigRepository;

  @Autowired
  private UserService userService;

  private final SecureRandom random = new SecureRandom();

  @Transactional
  public OtpCodeEntity generateOtp(String login, String operationId) {
    UserEntity user = userService.findByLogin(login)
            .orElseThrow(() -> new RuntimeException("User not found"));

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

    return otpCodeRepository.save(otpCode);
  }

  @Transactional
  public boolean validateOtp(String operationId, String code, String ipAddress) {
    Optional<OtpCodeEntity> otpOpt = otpCodeRepository.findByOperationIdAndStatus(
            operationId, OtpStatus.ACTIVE
    );

    if (otpOpt.isEmpty()) {
      return false;
    }

    OtpCodeEntity otpCode = otpOpt.get();

    if (otpCode.getExpiresAt().isBefore(LocalDateTime.now())) {
      otpCode.setStatus(OtpStatus.EXPIRED);
      otpCodeRepository.save(otpCode);
      return false;
    }

    if (otpCode.getAttemptsLeft() <= 0) {
      otpCode.setStatus(OtpStatus.EXPIRED);
      otpCodeRepository.save(otpCode);
      return false;
    }

    if (!otpCode.getCode().equals(code)) {
      otpCode.setAttemptsLeft(otpCode.getAttemptsLeft() - 1);
      otpCodeRepository.save(otpCode);
      return false;
    }

    otpCode.setStatus(OtpStatus.USED);
    otpCode.setConfirmedAt(LocalDateTime.now());
    otpCode.setConfirmedByIp(ipAddress);
    otpCodeRepository.save(otpCode);

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
}