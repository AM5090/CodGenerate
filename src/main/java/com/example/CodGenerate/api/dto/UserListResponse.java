package com.example.CodGenerate.api.dto;

public class UserListResponse {
  private Long id;
  private String login;
  private String role;
  private String createdAt;

  public UserListResponse(Long id, String login, String role, String createdAt) {
    this.id = id;
    this.login = login;
    this.role = role;
    this.createdAt = createdAt;
  }

  public Long getId() { return id; }
  public String getLogin() { return login; }
  public String getRole() { return role; }
  public String getCreatedAt() { return createdAt; }
}