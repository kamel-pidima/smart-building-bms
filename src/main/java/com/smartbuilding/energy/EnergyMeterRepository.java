package com.smartbuilding.energy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface EnergyMeterRepository extends JpaRepository<EnergyMeter, UUID> {

    List<EnergyMeter> findByBuildingId(UUID buildingId);

    List<EnergyMeter> findByBuildingIdAndMeterType(UUID buildingId, EnergyMeter.MeterType meterType);

    List<EnergyMeter> findByStatus(EnergyMeter.MeterStatus status);

    List<EnergyMeter> findByPowerFactorLessThan(BigDecimal powerFactor);
}
