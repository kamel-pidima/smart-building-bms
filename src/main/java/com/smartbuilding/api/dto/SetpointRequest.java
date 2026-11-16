package com.smartbuilding.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request DTO for setting a numeric setpoint
 */
@Data
public class SetpointRequest {
    
    @NotNull(message = "Value is required")
    private BigDecimal value;
}
