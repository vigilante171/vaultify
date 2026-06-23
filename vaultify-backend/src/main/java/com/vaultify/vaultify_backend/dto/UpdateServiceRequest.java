package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.ServiceCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateServiceRequest {

    @NotBlank(message = "Service name is required")
    private String name;

    private String description;

    @NotNull(message = "Service category is required")
    private ServiceCategory category;

    @Min(value = 0, message = "Price must be positive")
    private double price;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMinutes;

    private boolean active;
}
