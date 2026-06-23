package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private String appointmentId;

    private String businessId;

    private String clientId;

    private String clientName;

    private String clientPhone;

    private String serviceId;

    private String serviceName;

    private LocalDateTime appointmentDateTime;

    private AppointmentStatus status;

    private double price;

    private int durationMinutes;

    private String notes;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String message;
}
