package com.example.CodGenerate.dao.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String login;

  @Column(nullable = false)
  private String passwordHash;  // Хешированный пароль

  @Column(nullable = false)
  private String role;  // USER, ADMIN, MODERATOR и т.д.

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // Конструкторы
  public UserEntity() {}

  public UserEntity(String login, String passwordHash, String role) {
    this.login = login;
    this.passwordHash = passwordHash;
    this.role = role;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  // Геттеры и сеттеры
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getLogin() { return login; }
  public void setLogin(String login) { this.login = login; }

  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

  // Хелпер для обновления времени
  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}