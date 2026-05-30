package com.example.CodGenerate.dao.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_config")
public class OtpConfigEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer codeLength = 6;

  @Column(nullable = false)
  private Integer codeExpiryMinutes = 5;

  @Column(nullable = false)
  private Integer maxAttempts = 3;

  @Column(nullable = false)
  private Boolean isActive = true;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public OtpConfigEntity() {}

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Integer getCodeLength() { return codeLength; }
  public void setCodeLength(Integer codeLength) { this.codeLength = codeLength; }

  public Integer getCodeExpiryMinutes() { return codeExpiryMinutes; }
  public void setCodeExpiryMinutes(Integer codeExpiryMinutes) { this.codeExpiryMinutes = codeExpiryMinutes; }

  public Integer getMaxAttempts() { return maxAttempts; }
  public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }

  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}