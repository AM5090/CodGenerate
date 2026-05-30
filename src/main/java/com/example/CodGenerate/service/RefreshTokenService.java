package com.example.CodGenerate.service;

import com.example.CodGenerate.dao.entity.RefreshTokenEntity;
import com.example.CodGenerate.dao.entity.UserEntity;
import com.example.CodGenerate.dao.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

  @Value("${jwt.refresh-token-expiration}")
  private Long refreshTokenExpiration;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  public RefreshTokenEntity createRefreshToken(UserEntity user) {
    String token = UUID.randomUUID().toString();
    LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);
    RefreshTokenEntity refreshToken = new RefreshTokenEntity(token, user, expiresAt);
    return refreshTokenRepository.save(refreshToken);
  }

  public RefreshTokenEntity verifyRefreshToken(String token) {
    return refreshTokenRepository.findByTokenAndRevokedFalse(token)
            .orElseThrow(() -> new RuntimeException("Invalid or expired refresh token"));
  }

  @Transactional
  public void revokeRefreshToken(String token) {
    refreshTokenRepository.deleteByToken(token);
  }

  @Transactional
  public void revokeAllUserTokens(UserEntity user) {
    refreshTokenRepository.deleteByUser(user);
  }
}