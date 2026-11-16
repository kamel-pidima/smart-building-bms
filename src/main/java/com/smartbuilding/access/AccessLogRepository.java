package com.smartbuilding.access;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, UUID> {

    List<AccessLog> findByAccessControllerIdOrderByTimestampDesc(UUID accessControllerId);

    List<AccessLog> findByCredentialIdOrderByTimestampDesc(String credentialId);

    List<AccessLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<AccessLog> findByResult(AccessService.AccessResult result);
}
