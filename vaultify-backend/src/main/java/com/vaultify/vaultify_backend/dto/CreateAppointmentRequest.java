package com.vaultify.vaultify_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequest {

    @NotBlank(message = "Client ID is required")
    private String clientId;

    @NotBlank(message = "Service ID is required")
    private String serviceId;

    @NotNull(message = "Appointment date and time is required")
    private LocalDateTime appointmentDateTime;

    private String notes;
}
