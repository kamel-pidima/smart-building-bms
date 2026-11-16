package com.smartbuilding.api.controllers;

import com.smartbuilding.hvac.HvacController;
import com.smartbuilding.hvac.HvacService;
import com.smartbuilding.api.dto.SetpointRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * HVAC REST API Controller
 * 
 * Provides endpoints for HVAC monitoring and control
 */
@RestController
@RequestMapping("/api/v1/hvac")
@RequiredArgsConstructor
@Tag(name = "HVAC Control", description = "Heating, Ventilation, and Air Conditioning management")
public class HvacApiController {

    private final HvacService hvacService;

    @GetMapping("/buildings/{buildingId}/controllers")
    @Operation(summary = "Get all HVAC controllers for a building")
    public ResponseEntity<List<HvacController>> getControllers(@PathVariable UUID buildingId) {
        return ResponseEntity.ok(hvacService.getControllersByBuilding(buildingId));
    }

    @GetMapping("/zones/{zoneId}/controller")
    @Operation(summary = "Get HVAC controller for a specific zone")
    public ResponseEntity<HvacController> getZoneController(@PathVariable UUID zoneId) {
        return ResponseEntity.ok(hvacService.getControllerByZone(zoneId));
    }

    @PostMapping("/zones/{zoneId}/temperature")
    @Operation(summary = "Set temperature setpoint for a zone")
    public ResponseEntity<HvacController> setTemperature(
            @PathVariable UUID zoneId,
            @RequestBody SetpointRequest request) {
        return ResponseEntity.ok(hvacService.setTemperature(zoneId, request.getValue()));
    }

    @PostMapping("/controllers/{controllerId}/mode")
    @Operation(summary = "Set HVAC operating mode")
    public ResponseEntity<HvacController> setMode(
            @PathVariable UUID controllerId,
            @RequestParam HvacController.HvacMode mode) {
        return ResponseEntity.ok(hvacService.setMode(controllerId, mode));
    }

    @GetMapping("/offline")
    @Operation(summary = "Get controllers that are offline or unresponsive")
    public ResponseEntity<List<HvacController>> getOfflineControllers(
            @RequestParam(defaultValue = "5") int minutesThreshold) {
        return ResponseEntity.ok(hvacService.getOfflineControllers(minutesThreshold));
    }
}
