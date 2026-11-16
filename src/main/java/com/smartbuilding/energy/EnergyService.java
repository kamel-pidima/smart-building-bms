package com.smartbuilding.energy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Energy Management Service
 * 
 * Provides energy monitoring and analytics:
 * - Real-time consumption tracking
 * - Demand response management
 * - Cost analysis
 * - Peak shaving
 * - Carbon footprint calculation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EnergyService {

    private final EnergyMeterRepository meterRepository;
    
    private static final BigDecimal CARBON_FACTOR_KG_PER_KWH = new BigDecimal("0.42"); // kg CO2/kWh

    /**
     * Get current power consumption for a building
     */
    public BigDecimal getCurrentPowerKW(UUID buildingId) {
        List<EnergyMeter> meters = meterRepository.findByBuildingIdAndMeterType(buildingId, EnergyMeter.MeterType.MAIN);
        
        return meters.stream()
                .map(EnergyMeter::getActivePowerKW)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total energy consumption for a building (kWh)
     */
    public BigDecimal getTotalConsumptionKWh(UUID buildingId) {
        List<EnergyMeter> meters = meterRepository.findByBuildingIdAndMeterType(buildingId, EnergyMeter.MeterType.MAIN);
        
        return meters.stream()
                .map(EnergyMeter::getTotalKWh)
                .filter(e -> e != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate carbon footprint
     */
    public BigDecimal calculateCarbonFootprint(UUID buildingId) {
        BigDecimal totalKWh = getTotalConsumptionKWh(buildingId);
        return totalKWh.multiply(CARBON_FACTOR_KG_PER_KWH).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Update meter readings from telemetry
     */
    public void updateMeterReading(UUID meterId, BigDecimal activePower, BigDecimal totalKWh, BigDecimal powerFactor) {
        meterRepository.findById(meterId).ifPresent(meter -> {
            meter.setActivePowerKW(activePower);
            meter.setTotalKWh(totalKWh);
            meter.setPowerFactor(powerFactor);
            meter.setLastReading(LocalDateTime.now());
            meter.setStatus(EnergyMeter.MeterStatus.ONLINE);
            
            // Track peak demand
            if (meter.getPeakDemandKW() == null || activePower.compareTo(meter.getPeakDemandKW()) > 0) {
                meter.setPeakDemandKW(activePower);
            }
            
            meterRepository.save(meter);
            
            log.debug("Updated meter {}: power={}kW, total={}kWh, PF={}", 
                    meterId, activePower, totalKWh, powerFactor);
        });
    }

    /**
     * Get power quality issues (low power factor)
     */
    public List<EnergyMeter> getPowerQualityIssues(BigDecimal minPowerFactor) {
        return meterRepository.findByPowerFactorLessThan(minPowerFactor);
    }

    /**
     * Get building energy summary
     */
    public EnergySummary getBuildingSummary(UUID buildingId) {
        BigDecimal currentPower = getCurrentPowerKW(buildingId);
        BigDecimal totalConsumption = getTotalConsumptionKWh(buildingId);
        BigDecimal carbonFootprint = calculateCarbonFootprint(buildingId);
        
        List<EnergyMeter> meters = meterRepository.findByBuildingId(buildingId);
        BigDecimal peakDemand = meters.stream()
                .map(EnergyMeter::getPeakDemandKW)
                .filter(p -> p != null)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        return new EnergySummary(currentPower, totalConsumption, peakDemand, carbonFootprint);
    }

    public record EnergySummary(
            BigDecimal currentPowerKW,
            BigDecimal totalConsumptionKWh,
            BigDecimal peakDemandKW,
            BigDecimal carbonFootprintKg
    ) {}
}
