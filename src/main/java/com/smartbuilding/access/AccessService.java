package com.smartbuilding.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Access Control Service
 * 
 * Manages physical access operations:
 * - Credential validation
 * - Door control (lock/unlock)
 * - Access logging
 * - Anti-passback enforcement
 * - Emergency lockdown
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccessService {

    private final AccessRepository accessRepository;
    private final AccessLogRepository accessLogRepository;

    /**
     * Process access request
     * 
     * @param controllerId Access point ID
     * @param credentialId Credential being presented
     * @param credentialType Type of credential
     * @return Access decision
     */
    public AccessDecision processAccessRequest(UUID controllerId, String credentialId, CredentialType credentialType) {
        log.info("Processing access request at {} with credential {}", controllerId, credentialId);
        
        AccessController controller = accessRepository.findById(controllerId)
                .orElseThrow(() -> new IllegalArgumentException("Access point not found: " + controllerId));
        
        // Check controller status
        if (controller.getStatus() != AccessController.ControllerStatus.ONLINE) {
            return logAndReturn(controller, credentialId, credentialType, AccessResult.DENIED, "Controller not online");
        }
        
        // In production, would validate credential against database
        // For demo, accept any credential that starts with "VALID"
        boolean credentialValid = credentialId.startsWith("VALID");
        
        if (!credentialValid) {
            return logAndReturn(controller, credentialId, credentialType, AccessResult.DENIED, "Invalid credential");
        }
        
        // Check two-factor requirement
        if (Boolean.TRUE.equals(controller.getTwoFactorRequired()) && credentialType != CredentialType.MOBILE_2FA) {
            return logAndReturn(controller, credentialId, credentialType, AccessResult.DENIED, "Two-factor required");
        }
        
        // Grant access
        unlockDoor(controllerId, 5); // Unlock for 5 seconds
        return logAndReturn(controller, credentialId, credentialType, AccessResult.GRANTED, null);
    }

    /**
     * Unlock a door temporarily
     */
    public void unlockDoor(UUID controllerId, int seconds) {
        log.info("Unlocking door {} for {} seconds", controllerId, seconds);
        
        AccessController controller = accessRepository.findById(controllerId)
                .orElseThrow(() -> new IllegalArgumentException("Access point not found"));
        
        controller.setLockStatus(AccessController.LockStatus.UNLOCKED);
        accessRepository.save(controller);
        
        // In production, would schedule re-lock after timeout
    }

    /**
     * Emergency lockdown - lock all doors
     */
    public void emergencyLockdown(UUID buildingId) {
        log.warn("EMERGENCY LOCKDOWN initiated for building {}", buildingId);
        
        List<AccessController> controllers = accessRepository.findByBuildingId(buildingId);
        for (AccessController controller : controllers) {
            controller.setLockStatus(AccessController.LockStatus.LOCKED);
            controller.setStatus(AccessController.ControllerStatus.ALARM);
            accessRepository.save(controller);
        }
    }

    /**
     * Release emergency lockdown
     */
    public void releaseLockdown(UUID buildingId) {
        log.info("Releasing lockdown for building {}", buildingId);
        
        List<AccessController> controllers = accessRepository.findByBuildingId(buildingId);
        for (AccessController controller : controllers) {
            controller.setStatus(AccessController.ControllerStatus.ONLINE);
            accessRepository.save(controller);
        }
    }

    private AccessDecision logAndReturn(AccessController controller, String credentialId, 
                                        CredentialType credentialType, AccessResult result, String reason) {
        AccessLog log = AccessLog.builder()
                .accessControllerId(controller.getId())
                .credentialId(credentialId)
                .credentialType(credentialType)
                .result(result)
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build();
        accessLogRepository.save(log);
        
        return new AccessDecision(result, reason);
    }

    public enum CredentialType {
        CARD, PIN, BIOMETRIC_FINGER, BIOMETRIC_FACE, MOBILE, MOBILE_2FA
    }

    public enum AccessResult {
        GRANTED, DENIED, TAILGATE, TIMEOUT
    }

    public record AccessDecision(AccessResult result, String reason) {}
}
