package com.example.CodGenerate.api.dto;

public class OtpConfigResponse {
  private Integer codeLength;
  private Integer codeExpiryMinutes;
  private Integer maxAttempts;
  private Boolean isActive;
  private String updatedAt;

  public OtpConfigResponse(Integer codeLength, Integer codeExpiryMinutes,
                           Integer maxAttempts, Boolean isActive, String updatedAt) {
    this.codeLength = codeLength;
    this.codeExpiryMinutes = codeExpiryMinutes;
    this.maxAttempts = maxAttempts;
    this.isActive = isActive;
    this.updatedAt = updatedAt;
  }

  public Integer getCodeLength() { return codeLength; }
  public Integer getCodeExpiryMinutes() { return codeExpiryMinutes; }
  public Integer getMaxAttempts() { return maxAttempts; }
  public Boolean getIsActive() { return isActive; }
  public String getUpdatedAt() { return updatedAt; }
}