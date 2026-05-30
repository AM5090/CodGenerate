package com.example.CodGenerate.dao.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_codes", indexes = {
        @Index(name = "idx_operation_id", columnList = "operationId"),
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_code", columnList = "code"),
        @Index(name = "idx_expires_at", columnList = "expiresAt")
})
public class OtpCodeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String userId;

  @Column(nullable = false)
  private String operationId;

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  @Column(nullable = false)
  private Integer attemptsLeft;

  @Column(nullable = false)
  private String status; // ACTIVE, EXPIRED, USED

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime confirmedAt;

  private String confirmedByIp;

  // Конструкторы
  public OtpCodeEntity() {}

  public OtpCodeEntity(String userId, String operationId, String code,
                       LocalDateTime expiresAt, Integer maxAttempts) {
    this.userId = userId;
    this.operationId = operationId;
    this.code = code;
    this.expiresAt = expiresAt;
    this.attemptsLeft = maxAttempts;
    this.status = "PENDING";
    this.createdAt = LocalDateTime.now();
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }

  public String getOperationId() { return operationId; }
  public void setOperationId(String operationId) { this.operationId = operationId; }

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }

  public LocalDateTime getExpiresAt() { return expiresAt; }
  public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

  public Integer getAttemptsLeft() { return attemptsLeft; }
  public void setAttemptsLeft(Integer attemptsLeft) { this.attemptsLeft = attemptsLeft; }

  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getConfirmedAt() { return confirmedAt; }
  public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

  public String getConfirmedByIp() { return confirmedByIp; }
  public void setConfirmedByIp(String confirmedByIp) { this.confirmedByIp = confirmedByIp; }
}