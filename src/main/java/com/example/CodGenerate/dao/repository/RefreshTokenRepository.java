package com.example.CodGenerate.dao.repository;

import com.example.CodGenerate.dao.entity.RefreshTokenEntity;
import com.example.CodGenerate.dao.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
  Optional<RefreshTokenEntity> findByTokenAndRevokedFalse(String token);
  void deleteByUser(UserEntity user);
  void deleteByToken(String token);
}