package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.AppointmentResponse;
import com.vaultify.vaultify_backend.dto.CreateAppointmentRequest;
import com.vaultify.vaultify_backend.dto.UpdateAppointmentRequest;
import com.vaultify.vaultify_backend.service.OwnerAppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OwnerAppointmentController {

    private final OwnerAppointmentService ownerAppointmentService;

    @PostMapping
    public AppointmentResponse createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        return ownerAppointmentService.createAppointment(request);
    }

    @GetMapping
    public List<AppointmentResponse> getMyAppointments() {
        return ownerAppointmentService.getMyAppointments();
    }

    @GetMapping("/{appointmentId}")
    public AppointmentResponse getAppointmentById(@PathVariable String appointmentId) {
        return ownerAppointmentService.getAppointmentById(appointmentId);
    }

    @PutMapping("/{appointmentId}")
    public AppointmentResponse updateAppointment(
            @PathVariable String appointmentId,
            @Valid @RequestBody UpdateAppointmentRequest request
    ) {
        return ownerAppointmentService.updateAppointment(appointmentId, request);
    }

    @DeleteMapping("/{appointmentId}")
    public AppointmentResponse deleteAppointment(@PathVariable String appointmentId) {
        return ownerAppointmentService.deleteAppointment(appointmentId);
    }
}
