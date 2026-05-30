package com.example.CodGenerate.api.dto;

public class OtpGenerateRequest {
  private String operationId;
  private String channel; // sms, email, telegram, file

  public String getOperationId() { return operationId; }
  public void setOperationId(String operationId) { this.operationId = operationId; }

  public String getChannel() { return channel; }
  public void setChannel(String channel) { this.channel = channel; }
}