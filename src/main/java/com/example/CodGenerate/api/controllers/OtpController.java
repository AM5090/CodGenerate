package com.example.CodGenerate.api.controllers;

import com.example.CodGenerate.api.dto.OtpGenerateRequest;
import com.example.CodGenerate.api.dto.OtpResponse;
import com.example.CodGenerate.api.dto.OtpValidateRequest;
import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import com.example.CodGenerate.dao.entity.UserEntity;
import com.example.CodGenerate.service.OtpDeliveryService;
import com.example.CodGenerate.service.OtpService;
import com.example.CodGenerate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

  @Autowired
  private OtpService otpService;

  @Autowired
  private OtpDeliveryService otpDeliveryService;

  @Autowired
  private UserService userService;

  @PostMapping("/generate")
  public ResponseEntity<?> generateOtp(@RequestBody OtpGenerateRequest request,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       HttpServletRequest httpRequest) {

    try {
      String login = userDetails.getUsername();
      UserEntity user = userService.findByLogin(login).get();

      OtpCodeEntity otpCode = otpService.generateOtp(login, request.getOperationId());

      String destination = getDestinationForChannel(user, request.getChannel(), httpRequest);

      boolean sent = otpDeliveryService.send(otpCode, request.getChannel(), destination);

      if (!sent) {
        return ResponseEntity.status(500).body(Map.of("error", "Failed to send OTP"));
      }

      OtpResponse response = new OtpResponse(
              otpCode.getOperationId(),
              "OTP code sent successfully",
              otpCode.getExpiresAt().toString(),
              request.getChannel()
      );

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/validate")
  public ResponseEntity<?> validateOtp(@RequestBody OtpValidateRequest request,
                                       HttpServletRequest httpRequest) {

    String ipAddress = httpRequest.getRemoteAddr();
    boolean isValid = otpService.validateOtp(request.getOperationId(), request.getCode(), ipAddress);

    if (isValid) {
      return ResponseEntity.ok(Map.of("valid", true, "message", "OTP code is valid"));
    } else {
      return ResponseEntity.status(401).body(Map.of("valid", false, "message", "Invalid or expired OTP code"));
    }
  }

  @GetMapping("/status/{operationId}")
  public ResponseEntity<?> getOtpStatus(@PathVariable String operationId) {
    var status = otpService.getOtpStatus(operationId);
    if (status == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(Map.of("operationId", operationId, "status", status));
  }

  private String getDestinationForChannel(UserEntity user, String channel, HttpServletRequest request) {
    return switch (channel.toLowerCase()) {
      case "sms" -> "+7XXXXXXXXXX"; // заглушка
      case "email" -> user.getLogin() + "@example.com"; // заглушка
      case "telegram" -> "user_telegram_id"; // заглушка
      case "file" -> "otp_" + System.currentTimeMillis();
      default -> "unknown";
    };
  }
}