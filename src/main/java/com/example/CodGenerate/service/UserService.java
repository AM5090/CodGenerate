package com.example.CodGenerate.service;

import com.example.CodGenerate.dao.entity.UserEntity;
import com.example.CodGenerate.dao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public UserEntity register(String login, String rawPassword, String role) {
    if (userRepository.existsByLogin(login)) {
      throw new RuntimeException("Login already exists");
    }

    String hashedPassword = passwordEncoder.encode(rawPassword);

    if (role == null || role.isBlank()) {
      role = "USER";
    }

    UserEntity user = new UserEntity(login, hashedPassword, role);
    return userRepository.save(user);
  }

  public Optional<UserEntity> authenticate(String login, String rawPassword) {
    Optional<UserEntity> userOpt = userRepository.findByLogin(login);

    if (userOpt.isPresent()) {
      UserEntity user = userOpt.get();
      if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
        return Optional.of(user);
      }
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
}
