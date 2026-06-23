package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {

    private String serviceId;

    private String businessId;

    private String name;

    private String description;

    private ServiceCategory category;

    private double price;

    private int durationMinutes;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String message;
}
