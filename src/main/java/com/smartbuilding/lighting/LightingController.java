package com.smartbuilding.lighting;

import com.smartbuilding.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Lighting Controller Entity
 * 
 * Manages lighting fixtures and scenes for building zones.
 * Supports:
 * - Dimming control (0-100%)
 * - Color temperature adjustment (2700K-6500K)
 * - RGB color control
 * - Scene management
 * - Daylight harvesting integration
 */
@Entity
@Table(name = "lighting_controllers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LightingController extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Column(name = "zone_id")
    private UUID zoneId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LightingProtocol protocol;

    @Column(name = "protocol_address")
    private String protocolAddress;

    @Column(name = "brightness_level")
    private Integer brightnessLevel; // 0-100%

    @Column(name = "color_temperature")
    private Integer colorTemperature; // 2700K-6500K

    @Column(name = "rgb_red")
    private Integer rgbRed; // 0-255

    @Column(name = "rgb_green")
    private Integer rgbGreen; // 0-255

    @Column(name = "rgb_blue")
    private Integer rgbBlue; // 0-255

    @Column(name = "current_scene")
    private String currentScene;

    @Column(name = "daylight_harvesting_enabled")
    private Boolean daylightHarvestingEnabled;

    @Column(name = "target_lux")
    private Integer targetLux;

    @Column(name = "current_lux")
    private Integer currentLux;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ControllerStatus status;

    public enum LightingProtocol {
        DALI_2, DMX512, ANALOG_0_10V, MQTT, ZIGBEE
    }

    public enum ControllerStatus {
        ON, OFF, DIMMED, FAULT, EMERGENCY
    }
}
