package com.smartbuilding.hvac;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * HVAC Control Service
 * 
 * Manages HVAC controllers including:
 * - Temperature setpoint management with PID control
 * - Zone-based climate control
 * - Schedule integration
 * - Protocol communication (BACnet, Modbus, MQTT)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HvacService {

    private final HvacRepository hvacRepository;

    /**
     * Get all HVAC controllers for a building
     */
    public List<HvacController> getControllersByBuilding(UUID buildingId) {
        log.debug("Fetching HVAC controllers for building: {}", buildingId);
        return hvacRepository.findByBuildingId(buildingId);
    }

    /**
     * Get HVAC controller by zone
     */
    public HvacController getControllerByZone(UUID zoneId) {
        return hvacRepository.findByZoneId(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("No HVAC controller found for zone: " + zoneId));
    }

    /**
     * Set temperature setpoint for a zone
     * 
     * @param zoneId Target zone
     * @param temperature Target temperature in Celsius
     * @return Updated controller
     */
    public HvacController setTemperature(UUID zoneId, BigDecimal temperature) {
        log.info("Setting temperature for zone {} to {}°C", zoneId, temperature);
        
        HvacController controller = getControllerByZone(zoneId);
        
        // Validate temperature range (15-30°C)
        if (temperature.compareTo(new BigDecimal("15")) < 0 || 
            temperature.compareTo(new BigDecimal("30")) > 0) {
            throw new IllegalArgumentException("Temperature must be between 15°C and 30°C");
        }
        
        controller.setTargetTemperature(temperature);
        
        // Auto-select mode based on current vs target
        if (controller.getCurrentTemperature() != null) {
            if (controller.getCurrentTemperature().compareTo(temperature) > 0) {
                controller.setMode(HvacController.HvacMode.COOLING);
            } else if (controller.getCurrentTemperature().compareTo(temperature) < 0) {
                controller.setMode(HvacController.HvacMode.HEATING);
            }
        }
        
        return hvacRepository.save(controller);
    }

    /**
     * Set HVAC operating mode
     */
    public HvacController setMode(UUID controllerId, HvacController.HvacMode mode) {
        log.info("Setting HVAC mode for controller {} to {}", controllerId, mode);
        
        HvacController controller = hvacRepository.findById(controllerId)
                .orElseThrow(() -> new IllegalArgumentException("Controller not found: " + controllerId));
        
        controller.setMode(mode);
        return hvacRepository.save(controller);
    }

    /**
     * Update telemetry from controller
     */
    public void updateTelemetry(UUID controllerId, BigDecimal temperature, BigDecimal humidity) {
        hvacRepository.findById(controllerId).ifPresent(controller -> {
            controller.setCurrentTemperature(temperature);
            controller.setCurrentHumidity(humidity);
            controller.setLastCommunication(LocalDateTime.now());
            controller.setStatus(HvacController.ControllerStatus.ONLINE);
            hvacRepository.save(controller);
        });
    }

    /**
     * Get controllers that haven't communicated recently (offline detection)
     */
    public List<HvacController> getOfflineControllers(int minutesThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutesThreshold);
        return hvacRepository.findByLastCommunicationBeforeOrLastCommunicationIsNull(threshold);
    }
}
