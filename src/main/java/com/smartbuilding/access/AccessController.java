package com.smartbuilding.access;

import com.smartbuilding.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Access Control Point Entity
 * 
 * Represents a physical access point (door, gate, turnstile) with:
 * - Multiple credential types (card, PIN, biometric, mobile)
 * - Anti-passback support
 * - Time-based access rules
 * - Emergency egress handling
 */
@Entity
@Table(name = "access_controllers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessController extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Column(name = "zone_id")
    private UUID zoneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_point_type", nullable = false)
    private AccessPointType accessPointType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReaderProtocol protocol;

    @Column(name = "protocol_address")
    private String protocolAddress;

    @Column(name = "door_position")
    @Enumerated(EnumType.STRING)
    private DoorPosition doorPosition;

    @Column(name = "lock_status")
    @Enumerated(EnumType.STRING)
    private LockStatus lockStatus;

    @Column(name = "anti_passback_enabled")
    private Boolean antiPassbackEnabled;

    @Column(name = "two_factor_required")
    private Boolean twoFactorRequired;

    @Column(name = "rex_enabled")
    private Boolean rexEnabled; // Request-to-Exit

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ControllerStatus status;

    public enum AccessPointType {
        DOOR, GATE, TURNSTILE, ELEVATOR, PARKING_BARRIER
    }

    public enum ReaderProtocol {
        WIEGAND_26, WIEGAND_34, OSDP_V2, RS485, IP_BASED
    }

    public enum DoorPosition {
        OPEN, CLOSED, HELD_OPEN, FORCED_OPEN, UNKNOWN
    }

    public enum LockStatus {
        LOCKED, UNLOCKED, FAIL_SECURE, FAIL_SAFE
    }

    public enum ControllerStatus {
        ONLINE, OFFLINE, ALARM, TAMPER, MAINTENANCE
    }
}
