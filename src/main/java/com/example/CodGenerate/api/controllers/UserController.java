package com.example.CodGenerate.api.controllers;

import com.example.CodGenerate.api.dto.LoginRequest;
import com.example.CodGenerate.api.dto.RegisterRequest;
import com.example.CodGenerate.api.dto.UserResponse;
import com.example.CodGenerate.dao.entity.UserEntity;
import com.example.CodGenerate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
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

      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (RuntimeException e) {
      Map<String, String> error = new HashMap<>();
      error.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(error);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    var userOpt = userService.authenticate(request.getLogin(), request.getPassword());

    if (userOpt.isPresent()) {
      UserEntity user = userOpt.get();
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Login successful");
      response.put("user", new UserResponse(
              user.getId(),
              user.getLogin(),
              user.getRole(),
              user.getCreatedAt().toString()
      ));

      // TODO: добавить JWT токен здесь

      return ResponseEntity.ok(response);
    } else {
      Map<String, String> error = new HashMap<>();
      error.put("error", "Invalid login or password");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
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