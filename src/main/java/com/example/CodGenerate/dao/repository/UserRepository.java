package com.example.CodGenerate.dao.repository;

import com.example.CodGenerate.dao.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByLogin(String login);

  boolean existsByLogin(String login);

  void deleteByLogin(String login);

  boolean existsByRole(String role);

  List<UserEntity> findAllByRoleNot(String role);

  @Modifying
  @Transactional
  @Query("DELETE FROM UserEntity u WHERE u.login = :login")
  void deleteUserByLogin(String login);
}