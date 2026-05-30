package com.example.CodGenerate.api.controllers;

import com.example.CodGenerate.api.dto.OtpConfigRequest;
import com.example.CodGenerate.api.dto.OtpConfigResponse;
import com.example.CodGenerate.api.dto.UserListResponse;
import com.example.CodGenerate.dao.entity.OtpConfigEntity;
import com.example.CodGenerate.dao.entity.UserEntity;
import com.example.CodGenerate.dao.repository.UserRepository;
import com.example.CodGenerate.service.OtpService;
import com.example.CodGenerate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private OtpService otpService;

  @GetMapping("/users")
  public ResponseEntity<?> getAllNonAdminUsers() {
    List<UserEntity> users = userRepository.findAllByRoleNot("ADMIN");

    List<UserListResponse> response = users.stream()
            .map(user -> new UserListResponse(
                    user.getId(),
                    user.getLogin(),
                    user.getRole(),
                    user.getCreatedAt().toString()
            ))
            .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/users/{login}")
  public ResponseEntity<?> deleteUser(@PathVariable String login) {
    try {
      userService.deleteUser(login);
      Map<String, String> response = new HashMap<>();
      response.put("message", "User '" + login + "' deleted successfully");
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      Map<String, String> error = new HashMap<>();
      error.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(error);
    }
  }

  @PutMapping("/otp/config")
  public ResponseEntity<?> updateOtpConfig(@RequestBody OtpConfigRequest request) {
    try {
      OtpConfigEntity updatedConfig = otpService.updateConfig(
              request.getCodeLength(),
              request.getCodeExpiryMinutes(),
              request.getMaxAttempts()
      );

      OtpConfigResponse response = new OtpConfigResponse(
              updatedConfig.getCodeLength(),
              updatedConfig.getCodeExpiryMinutes(),
              updatedConfig.getMaxAttempts(),
              updatedConfig.getIsActive(),
              updatedConfig.getUpdatedAt() != null ? updatedConfig.getUpdatedAt().toString() : null
      );

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, String> error = new HashMap<>();
      error.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(error);
    }
  }


  @GetMapping("/otp/config")
  public ResponseEntity<?> getOtpConfig() {
    OtpConfigEntity config = otpService.getCurrentConfig();
    OtpConfigResponse response = new OtpConfigResponse(
            config.getCodeLength(),
            config.getCodeExpiryMinutes(),
            config.getMaxAttempts(),
            config.getIsActive(),
            config.getUpdatedAt() != null ? config.getUpdatedAt().toString() : null
    );
    return ResponseEntity.ok(response);
  }
}