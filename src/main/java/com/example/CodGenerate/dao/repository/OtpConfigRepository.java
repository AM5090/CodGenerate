package com.example.CodGenerate.dao.repository;

import com.example.CodGenerate.dao.entity.OtpConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OtpConfigRepository extends JpaRepository<OtpConfigEntity, Long> {

  default OtpConfigEntity getSingleConfig() {
    var all = findAll();
    if (all.isEmpty()) {
      return save(new OtpConfigEntity());
    }
    return all.get(0);
  }

  @Modifying
  @Transactional
  @Query("UPDATE OtpConfigEntity c SET c.codeLength = :codeLength, " +
          "c.codeExpiryMinutes = :expiryMinutes, " +
          "c.maxAttempts = :maxAttempts, " +
          "c.isActive = :isActive " +
          "WHERE c.id = :id")
  int updateConfig(Long id, Integer codeLength, Integer expiryMinutes,
                   Integer maxAttempts, Boolean isActive);
}