package com.smartbuilding.lighting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Lighting Control Service
 * 
 * Manages lighting operations including:
 * - Brightness and color control
 * - Scene activation
 * - Daylight harvesting
 * - Emergency lighting
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LightingService {

    private final LightingRepository lightingRepository;

    /**
     * Set brightness level for a zone
     */
    public LightingController setBrightness(UUID zoneId, int brightness) {
        log.info("Setting brightness for zone {} to {}%", zoneId, brightness);
        
        if (brightness < 0 || brightness > 100) {
            throw new IllegalArgumentException("Brightness must be between 0 and 100");
        }
        
        LightingController controller = lightingRepository.findByZoneId(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("No lighting controller for zone: " + zoneId));
        
        controller.setBrightnessLevel(brightness);
        controller.setStatus(brightness == 0 
                ? LightingController.ControllerStatus.OFF 
                : LightingController.ControllerStatus.DIMMED);
        
        return lightingRepository.save(controller);
    }

    /**
     * Activate a lighting scene
     */
    public LightingController activateScene(UUID zoneId, String sceneName) {
        log.info("Activating scene '{}' for zone {}", sceneName, zoneId);
        
        LightingController controller = lightingRepository.findByZoneId(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("No lighting controller for zone: " + zoneId));
        
        // Apply scene settings (in real implementation, would load from scene definitions)
        controller.setCurrentScene(sceneName);
        
        switch (sceneName.toLowerCase()) {
            case "daylight" -> {
                controller.setBrightnessLevel(100);
                controller.setColorTemperature(6500);
            }
            case "evening" -> {
                controller.setBrightnessLevel(70);
                controller.setColorTemperature(3000);
            }
            case "presentation" -> {
                controller.setBrightnessLevel(80);
                controller.setColorTemperature(4000);
            }
            case "off" -> {
                controller.setBrightnessLevel(0);
                controller.setStatus(LightingController.ControllerStatus.OFF);
            }
            default -> log.warn("Unknown scene: {}", sceneName);
        }
        
        return lightingRepository.save(controller);
    }

    /**
     * Enable/disable daylight harvesting
     */
    public LightingController setDaylightHarvesting(UUID zoneId, boolean enabled, Integer targetLux) {
        log.info("Setting daylight harvesting for zone {}: enabled={}, targetLux={}", zoneId, enabled, targetLux);
        
        LightingController controller = lightingRepository.findByZoneId(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("No lighting controller for zone: " + zoneId));
        
        controller.setDaylightHarvestingEnabled(enabled);
        if (targetLux != null) {
            controller.setTargetLux(targetLux);
        }
        
        return lightingRepository.save(controller);
    }

    /**
     * Adjust brightness based on current lux level (daylight harvesting)
     */
    public void adjustForDaylight(UUID controllerId, int currentLux) {
        lightingRepository.findById(controllerId).ifPresent(controller -> {
            if (Boolean.TRUE.equals(controller.getDaylightHarvestingEnabled()) 
                    && controller.getTargetLux() != null) {
                
                controller.setCurrentLux(currentLux);
                
                // Calculate required artificial light
                int deficit = controller.getTargetLux() - currentLux;
                int brightness = Math.max(0, Math.min(100, deficit * 100 / controller.getTargetLux()));
                
                controller.setBrightnessLevel(brightness);
                lightingRepository.save(controller);
                
                log.debug("Daylight harvesting: currentLux={}, targetLux={}, brightness={}%",
                        currentLux, controller.getTargetLux(), brightness);
            }
        });
    }

    /**
     * Get all controllers in fault state
     */
    public List<LightingController> getFaultedControllers() {
        return lightingRepository.findByStatus(LightingController.ControllerStatus.FAULT);
    }
}
