package com.smartbuilding.hvac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for HVAC Controller entities
 */
@Repository
public interface HvacRepository extends JpaRepository<HvacController, UUID> {

    List<HvacController> findByBuildingId(UUID buildingId);

    Optional<HvacController> findByZoneId(UUID zoneId);

    List<HvacController> findByStatus(HvacController.ControllerStatus status);

    List<HvacController> findByLastCommunicationBeforeOrLastCommunicationIsNull(LocalDateTime threshold);

    List<HvacController> findByProtocol(HvacController.Protocol protocol);
}
