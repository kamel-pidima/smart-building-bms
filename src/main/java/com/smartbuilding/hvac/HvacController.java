package com.smartbuilding.hvac;

import com.smartbuilding.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * HVAC Controller Entity
 * 
 * Represents a physical HVAC controller unit that manages temperature,
 * humidity, and ventilation for one or more zones in the building.
 * 
 * Supports multiple communication protocols:
 * - BACnet/IP for enterprise systems
 * - Modbus RTU/TCP for legacy equipment
 * - MQTT for IoT sensors
 */
@Entity
@Table(name = "hvac_controllers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HvacController extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Column(name = "zone_id")
    private UUID zoneId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HvacMode mode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Protocol protocol;

    @Column(name = "protocol_address")
    private String protocolAddress;

    @Column(name = "current_temperature", precision = 5, scale = 2)
    private BigDecimal currentTemperature;

    @Column(name = "target_temperature", precision = 5, scale = 2)
    private BigDecimal targetTemperature;

    @Column(name = "current_humidity", precision = 5, scale = 2)
    private BigDecimal currentHumidity;

    @Column(name = "target_humidity", precision = 5, scale = 2)
    private BigDecimal targetHumidity;

    @Column(name = "fan_speed")
    private Integer fanSpeed; // 0-100%

    @Column(name = "damper_position")
    private Integer damperPosition; // 0-100%

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ControllerStatus status;

    @Column(name = "last_communication")
    private java.time.LocalDateTime lastCommunication;

    public enum HvacMode {
        OFF, HEATING, COOLING, AUTO, FAN_ONLY, DEHUMIDIFY
    }

    public enum Protocol {
        BACNET_IP, MODBUS_RTU, MODBUS_TCP, MQTT
    }

    public enum ControllerStatus {
        ONLINE, OFFLINE, FAULT, MAINTENANCE
    }
}
