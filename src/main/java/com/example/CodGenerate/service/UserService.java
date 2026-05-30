package com.example.CodGenerate.service;

import com.example.CodGenerate.dao.entity.UserEntity;
import com.example.CodGenerate.dao.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public UserEntity register(String login, String rawPassword, String role) {
    log.info("Attempting to register user: {}", login);

    if (userRepository.existsByLogin(login)) {
      log.warn("Registration failed - login already exists: {}", login);
      throw new RuntimeException("Login already exists");
    }

    if ("ADMIN".equalsIgnoreCase(role)) {
      if (userRepository.existsByRole("ADMIN")) {
        log.warn("Registration failed - attempt to create second admin: {}", login);
        throw new RuntimeException("Admin already exists in system. Cannot create second admin.");
      }
    }


    if (role == null || role.isBlank()) {
      role = "USER";
    }

    String hashedPassword = passwordEncoder.encode(rawPassword);
    UserEntity user = new UserEntity(login, hashedPassword, role);
    UserEntity saved = userRepository.save(user);
    log.info("User registered successfully: {} with role: {}", login, role);
    return saved;
  }

  public Optional<UserEntity> authenticate(String login, String rawPassword) {
    log.debug("Authentication attempt for user: {}", login);

    Optional<UserEntity> userOpt = userRepository.findByLogin(login);

    if (userOpt.isPresent()) {
      UserEntity user = userOpt.get();
      if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
        log.info("User authenticated successfully: {}", login);
        return Optional.of(user);
      }
      log.warn("Authentication failed - wrong password for user: {}", login);
    } else {
      log.warn("Authentication failed - user not found: {}", login);
    }
    return Optional.empty();
  }

  public Optional<UserEntity> findByLogin(String login) {
    return userRepository.findByLogin(login);
  }

  public Optional<UserEntity> findById(Long id) {
    return userRepository.findById(id);
  }

  public boolean existsByLogin(String login) {
    return userRepository.existsByLogin(login);
  }

  public boolean adminExists() {
    return userRepository.existsByRole("ADMIN");
  }

  @Transactional
  public void deleteUser(String login) {
    log.info("Attempting to delete user: {}", login);

    UserEntity user = userRepository.findByLogin(login)
            .orElseThrow(() -> {
              log.error("User not found for deletion: {}", login);
              return new RuntimeException("User not found: " + login);
            });

    if ("ADMIN".equals(user.getRole()) && userRepository.existsByRole("ADMIN")) {
      long adminCount = userRepository.findAllByRoleNot("USER").size();
      if (adminCount <= 1) {
        throw new RuntimeException("Cannot delete the only admin");
      }
    }

    userRepository.deleteByLogin(login);
    log.info("User deleted successfully: {}", login);
  }
}
