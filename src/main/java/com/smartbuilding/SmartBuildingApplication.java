package com.smartbuilding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Smart Building Management System - Main Application Entry Point
 * 
 * This system provides integrated building management with:
 * - HVAC Control: Temperature, humidity, ventilation management
 * - Lighting Automation: Scene control, daylight harvesting, scheduling
 * - Access Control: Card readers, biometrics, visitor management
 * - Energy Monitoring: Consumption tracking, demand response, analytics
 * 
 * @author Smart Building Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class SmartBuildingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartBuildingApplication.class, args);
    }
}
