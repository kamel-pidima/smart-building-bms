package com.smartbuilding.lighting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Lighting Controller entities
 */
@Repository
public interface LightingRepository extends JpaRepository<LightingController, UUID> {

    List<LightingController> findByBuildingId(UUID buildingId);

    Optional<LightingController> findByZoneId(UUID zoneId);

    List<LightingController> findByStatus(LightingController.ControllerStatus status);

    List<LightingController> findByDaylightHarvestingEnabledTrue();

    List<LightingController> findByCurrentScene(String sceneName);
}
