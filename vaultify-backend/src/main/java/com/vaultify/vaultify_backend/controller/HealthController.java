package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.HealthResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/health")
    public HealthResponse health() {
        return HealthResponse.builder()
                .status("UP")
                .application("Vaultify Backend")
                .version("1.0.0")
                .message("Vaultify backend is running successfully")
                .modules(List.of(
                        "Authentication",
                        "JWT Security",
                        "Super Admin Management",
                        "Business Management",
                        "Subscription Management",
                        "Owner Dashboard",
                        "Owner Profile",
                        "Business Profile",
                        "Staff Management",
                        "Clients Management",
                        "Services Management",
                        "Appointments Management",
                        "Staff Dashboard",
                        "Staff Read Access",
                        "Audit Logs",
                        "Search and Filters",
                        "Pagination"
                ))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/info")
    public HealthResponse info() {
        return HealthResponse.builder()
                .status("READY")
                .application("Vaultify Backend")
                .version("1.0.0")
                .message("Multi-tenant SaaS backend for hair salons and men barbers")
                .modules(List.of(
                        "SUPER_ADMIN APIs: businesses, users, subscriptions, audit logs, dashboard",
                        "OWNER APIs: dashboard, profile, business profile, clients, services, staff, appointments",
                        "STAFF APIs: dashboard, appointments, clients, services read access"
                ))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
