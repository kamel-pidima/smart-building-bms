package com.smartbuilding.access;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccessRepository extends JpaRepository<AccessController, UUID> {

    List<AccessController> findByBuildingId(UUID buildingId);

    List<AccessController> findByStatus(AccessController.ControllerStatus status);

    List<AccessController> findByDoorPosition(AccessController.DoorPosition position);
}
