package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.AppointmentResponse;
import com.vaultify.vaultify_backend.service.StaffAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffAppointmentController {

    private final StaffAppointmentService staffAppointmentService;

    @GetMapping
    public List<AppointmentResponse> getMyBusinessAppointments() {
        return staffAppointmentService.getMyBusinessAppointments();
    }

    @GetMapping("/today")
    public List<AppointmentResponse> getTodayAppointments() {
        return staffAppointmentService.getTodayAppointments();
    }

    @GetMapping("/{appointmentId}")
    public AppointmentResponse getAppointmentById(@PathVariable String appointmentId) {
        return staffAppointmentService.getAppointmentById(appointmentId);
    }
}
