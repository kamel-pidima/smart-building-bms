package com.smartbuilding.access;

import com.smartbuilding.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Access Log Entity
 * Records all access attempts for audit and analytics
 */
@Entity
@Table(name = "access_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLog extends BaseEntity {

    @Column(name = "access_controller_id", nullable = false)
    private UUID accessControllerId;

    @Column(name = "credential_id")
    private String credentialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "credential_type")
    private AccessService.CredentialType credentialType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessService.AccessResult result;

    private String reason;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private Direction direction;

    public enum Direction {
        ENTRY, EXIT, UNKNOWN
    }
}
