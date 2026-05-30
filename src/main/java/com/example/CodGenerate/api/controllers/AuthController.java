package com.example.CodGenerate.api.controllers;

import com.example.CodGenerate.api.dto.AuthResponse;
import com.example.CodGenerate.api.dto.RefreshTokenRequest;
import com.example.CodGenerate.dao.entity.RefreshTokenEntity;
import com.example.CodGenerate.dao.entity.UserEntity;
import com.example.CodGenerate.service.JwtService;
import com.example.CodGenerate.service.RefreshTokenService;
import com.example.CodGenerate.service.CustomUserDetailsService;
import com.example.CodGenerate.api.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private CustomUserDetailsService userDetailsService;

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
    try {
      RefreshTokenEntity refreshTokenEntity = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
      UserEntity user = refreshTokenEntity.getUser();

      UserDetails userDetails = userDetailsService.loadUserByUsername(user.getLogin());
      String newAccessToken = jwtService.generateAccessToken(userDetails);
      String newRefreshToken = jwtService.generateRefreshToken(userDetails);

      refreshTokenService.revokeRefreshToken(request.getRefreshToken());
      var newRefreshTokenEntity = refreshTokenService.createRefreshToken(user);

      AuthResponse response = new AuthResponse(
              newAccessToken,
              newRefreshTokenEntity.getToken(),
              new UserResponse(user.getId(), user.getLogin(), user.getRole(), user.getCreatedAt().toString())
      );

      return ResponseEntity.ok(response);

    } catch (RuntimeException e) {
      Map<String, String> error = new HashMap<>();
      error.put("error", e.getMessage());
      return ResponseEntity.status(401).body(error);
    }
  }
}