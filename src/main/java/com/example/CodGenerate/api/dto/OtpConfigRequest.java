package com.example.CodGenerate.api.dto;

public class OtpConfigRequest {
  private Integer codeLength;
  private Integer codeExpiryMinutes;
  private Integer maxAttempts;

  public Integer getCodeLength() { return codeLength; }
  public void setCodeLength(Integer codeLength) { this.codeLength = codeLength; }

  public Integer getCodeExpiryMinutes() { return codeExpiryMinutes; }
  public void setCodeExpiryMinutes(Integer codeExpiryMinutes) { this.codeExpiryMinutes = codeExpiryMinutes; }

  public Integer getMaxAttempts() { return maxAttempts; }
  public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }
}