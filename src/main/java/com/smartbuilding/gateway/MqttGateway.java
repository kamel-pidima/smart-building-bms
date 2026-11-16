package com.smartbuilding.gateway;

import com.smartbuilding.hvac.HvacService;
import com.smartbuilding.lighting.LightingService;
import com.smartbuilding.energy.EnergyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * MQTT Gateway for IoT Device Communication
 * 
 * Handles bidirectional communication with field devices:
 * - Subscribe to sensor telemetry
 * - Publish control commands
 * - Device status monitoring
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MqttGateway implements MqttCallback {

    private final HvacService hvacService;
    private final LightingService lightingService;
    private final EnergyService energyService;

    @Value("${mqtt.broker.url:tcp://localhost:1883}")
    private String brokerUrl;

    @Value("${mqtt.client.id:smart-building-gateway}")
    private String clientId;

    private MqttClient mqttClient;

    @PostConstruct
    public void init() {
        try {
            mqttClient = new MqttClient(brokerUrl, clientId);
            mqttClient.setCallback(this);
            
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);
            
            mqttClient.connect(options);
            
            // Subscribe to telemetry topics
            mqttClient.subscribe("building/+/hvac/+/telemetry", 1);
            mqttClient.subscribe("building/+/lighting/+/telemetry", 1);
            mqttClient.subscribe("building/+/energy/+/telemetry", 1);
            
            log.info("MQTT Gateway connected to {}", brokerUrl);
        } catch (MqttException e) {
            log.error("Failed to connect to MQTT broker: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                log.info("MQTT Gateway disconnected");
            }
        } catch (MqttException e) {
            log.error("Error disconnecting MQTT: {}", e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT connection lost: {}", cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        log.debug("Received message on topic {}: {}", topic, new String(message.getPayload()));
        
        try {
            String[] parts = topic.split("/");
            if (parts.length >= 5) {
                String deviceType = parts[2];
                UUID deviceId = UUID.fromString(parts[3]);
                
                // Parse telemetry (simplified JSON parsing)
                String payload = new String(message.getPayload());
                
                switch (deviceType) {
                    case "hvac" -> processHvacTelemetry(deviceId, payload);
                    case "lighting" -> processLightingTelemetry(deviceId, payload);
                    case "energy" -> processEnergyTelemetry(deviceId, payload);
                }
            }
        } catch (Exception e) {
            log.error("Error processing MQTT message: {}", e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Confirmation of message delivery
    }

    private void processHvacTelemetry(UUID deviceId, String payload) {
        // Parse temperature and humidity from payload
        // Format: {"temperature": 22.5, "humidity": 45.0}
        BigDecimal temp = extractDecimal(payload, "temperature");
        BigDecimal humidity = extractDecimal(payload, "humidity");
        hvacService.updateTelemetry(deviceId, temp, humidity);
    }

    private void processLightingTelemetry(UUID deviceId, String payload) {
        Integer lux = extractInteger(payload, "lux");
        if (lux != null) {
            lightingService.adjustForDaylight(deviceId, lux);
        }
    }

    private void processEnergyTelemetry(UUID deviceId, String payload) {
        BigDecimal power = extractDecimal(payload, "activePower");
        BigDecimal totalKwh = extractDecimal(payload, "totalKwh");
        BigDecimal pf = extractDecimal(payload, "powerFactor");
        energyService.updateMeterReading(deviceId, power, totalKwh, pf);
    }

    /**
     * Publish a command to a device
     */
    public void publishCommand(String topic, String payload) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.publish(topic, new MqttMessage(payload.getBytes()));
                log.debug("Published to {}: {}", topic, payload);
            }
        } catch (MqttException e) {
            log.error("Failed to publish MQTT message: {}", e.getMessage());
        }
    }

    // Simple JSON value extractors (in production, use Jackson)
    private BigDecimal extractDecimal(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*([\\d.]+)";
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
            if (m.find()) {
                return new BigDecimal(m.group(1));
            }
        } catch (Exception e) {
            log.warn("Failed to extract {} from payload", key);
        }
        return null;
    }

    private Integer extractInteger(String json, String key) {
        BigDecimal decimal = extractDecimal(json, key);
        return decimal != null ? decimal.intValue() : null;
    }
}
