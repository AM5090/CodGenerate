package com.example.CodGenerate.dao.repository;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCodeEntity, Long> {

  Optional<OtpCodeEntity> findByOperationIdAndStatus(String operationId, String status);

  Optional<OtpCodeEntity> findByCodeAndOperationId(String code, String operationId);

  List<OtpCodeEntity> findByUserIdAndStatusAndExpiresAtAfter(String userId, String status, LocalDateTime now);

  @Modifying
  @Transactional
  @Query("UPDATE OtpCodeEntity o SET o.status = 'EXPIRED' " +
          "WHERE o.status = 'PENDING' AND o.expiresAt < :now")
  int expireOldCodes(LocalDateTime now);
}