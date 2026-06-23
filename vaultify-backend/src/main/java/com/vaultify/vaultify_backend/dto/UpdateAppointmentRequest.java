package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateAppointmentRequest {

    @NotNull(message = "Appointment date and time is required")
    private LocalDateTime appointmentDateTime;

    @NotNull(message = "Appointment status is required")
    private AppointmentStatus status;

    private String notes;

    private boolean active;
}
