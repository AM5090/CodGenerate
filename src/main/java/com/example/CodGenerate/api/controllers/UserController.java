package com.example.CodGenerate.api.controllers;

import com.example.CodGenerate.api.dto.*;
import com.example.CodGenerate.dao.entity.UserEntity;
import com.example.CodGenerate.service.UserService;
import com.example.CodGenerate.service.JwtService;
import com.example.CodGenerate.service.RefreshTokenService;
import com.example.CodGenerate.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  private CustomUserDetailsService userDetailsService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    log.info("Received registration request for login: {}", request.getLogin());

    try {
      UserEntity user = userService.register(
              request.getLogin(),
              request.getPassword(),
              request.getRole()
      );

      UserResponse response = new UserResponse(
              user.getId(),
              user.getLogin(),
              user.getRole(),
              user.getCreatedAt().toString()
      );

      log.info("User registered successfully: {}", request.getLogin());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (RuntimeException e) {
      log.warn("Registration failed for {}: {}", request.getLogin(), e.getMessage());
      Map<String, String> error = new HashMap<>();
      error.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(error);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    log.info("Login attempt for user: {}", request.getLogin());

    try {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
      );

      UserEntity user = userService.findByLogin(request.getLogin()).get();
      UserDetails userDetails = userDetailsService.loadUserByUsername(request.getLogin());

      String accessToken = jwtService.generateAccessToken(userDetails);
      String refreshToken = jwtService.generateRefreshToken(userDetails);

      var refreshTokenEntity = refreshTokenService.createRefreshToken(user);

      AuthResponse response = new AuthResponse(
              accessToken,
              refreshTokenEntity.getToken(),
              new UserResponse(user.getId(), user.getLogin(), user.getRole(), user.getCreatedAt().toString())
      );

      log.info("User logged in successfully: {}", request.getLogin());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.warn("Login failed for {}: {}", request.getLogin(), e.getMessage());
      Map<String, String> error = new HashMap<>();
      error.put("error", "Invalid login or password");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      String username = jwtService.extractUsername(token);
      if (username != null) {
        userService.findByLogin(username).ifPresent(user ->
                refreshTokenService.revokeAllUserTokens(user)
        );
      }
    }
    return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
  }

  @GetMapping("/{login}")
  public ResponseEntity<?> getUserByLogin(@PathVariable String login) {
    var userOpt = userService.findByLogin(login);

    if (userOpt.isPresent()) {
      UserEntity user = userOpt.get();
      UserResponse response = new UserResponse(
              user.getId(),
              user.getLogin(),
              user.getRole(),
              user.getCreatedAt().toString()
      );
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}