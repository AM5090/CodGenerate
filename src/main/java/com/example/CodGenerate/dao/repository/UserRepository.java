package com.example.CodGenerate.dao.repository;

import com.example.CodGenerate.dao.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByLogin(String login);

  boolean existsByLogin(String login);

  void deleteByLogin(String login);
}