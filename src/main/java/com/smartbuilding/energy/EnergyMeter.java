package com.smartbuilding.energy;

import com.smartbuilding.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Energy Meter Entity
 * 
 * Represents electrical metering points for energy monitoring:
 * - Main building meters
 * - Sub-meters for zones/floors
 * - Equipment-level monitoring
 * - Power quality analysis
 */
@Entity
@Table(name = "energy_meters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyMeter extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Column(name = "zone_id")
    private UUID zoneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_type", nullable = false)
    private MeterType meterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Protocol protocol;

    @Column(name = "protocol_address")
    private String protocolAddress;

    // Real-time readings
    @Column(name = "voltage_l1", precision = 10, scale = 2)
    private BigDecimal voltageL1;

    @Column(name = "voltage_l2", precision = 10, scale = 2)
    private BigDecimal voltageL2;

    @Column(name = "voltage_l3", precision = 10, scale = 2)
    private BigDecimal voltageL3;

    @Column(name = "current_l1", precision = 10, scale = 3)
    private BigDecimal currentL1;

    @Column(name = "current_l2", precision = 10, scale = 3)
    private BigDecimal currentL2;

    @Column(name = "current_l3", precision = 10, scale = 3)
    private BigDecimal currentL3;

    @Column(name = "active_power_kw", precision = 12, scale = 3)
    private BigDecimal activePowerKW;

    @Column(name = "reactive_power_kvar", precision = 12, scale = 3)
    private BigDecimal reactivePowerKVAR;

    @Column(name = "power_factor", precision = 4, scale = 3)
    private BigDecimal powerFactor;

    @Column(name = "frequency_hz", precision = 5, scale = 2)
    private BigDecimal frequencyHz;

    // Cumulative readings
    @Column(name = "total_kwh", precision = 15, scale = 3)
    private BigDecimal totalKWh;

    @Column(name = "peak_demand_kw", precision = 12, scale = 3)
    private BigDecimal peakDemandKW;

    @Column(name = "last_reading")
    private java.time.LocalDateTime lastReading;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeterStatus status;

    public enum MeterType {
        MAIN, SUB_METER, EQUIPMENT, SOLAR_INVERTER, BATTERY
    }

    public enum Protocol {
        MODBUS_TCP, MODBUS_RTU, BACNET_IP, DLMS_COSEM, IEC_61850
    }

    public enum MeterStatus {
        ONLINE, OFFLINE, FAULT, CALIBRATING
    }
}
