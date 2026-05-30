package com.example.CodGenerate.dao.repository;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import com.example.CodGenerate.dao.type.OtpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCodeEntity, Long> {

  Optional<OtpCodeEntity> findByOperationIdAndStatus(String operationId, OtpStatus status);

  Optional<OtpCodeEntity> findByCodeAndOperationId(String code, String operationId);

  List<OtpCodeEntity> findByUserIdAndStatusAndExpiresAtAfter(Long userId, String status, LocalDateTime now);

  @Modifying
  @Transactional
  @Query("UPDATE OtpCodeEntity o SET o.status = :expired " +
          "WHERE o.status = :active AND o.expiresAt < :now")
  int expireOldCodes(@Param("expired") OtpStatus expired,
                     @Param("active") OtpStatus active,
                     @Param("now") LocalDateTime now);
}