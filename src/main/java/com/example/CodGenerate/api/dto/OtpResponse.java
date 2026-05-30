package com.example.CodGenerate.api.dto;

public class OtpResponse {
  private String operationId;
  private String message;
  private String expiresAt;
  private String deliveredVia;

  public OtpResponse(String operationId, String message, String expiresAt, String deliveredVia) {
    this.operationId = operationId;
    this.message = message;
    this.expiresAt = expiresAt;
    this.deliveredVia = deliveredVia;
  }

  public String getOperationId() { return operationId; }
  public String getMessage() { return message; }
  public String getExpiresAt() { return expiresAt; }
  public String getDeliveredVia() { return deliveredVia; }
}