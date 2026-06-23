package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.SuperAdminDashboardResponse;
import com.vaultify.vaultify_backend.service.SuperAdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SuperAdminDashboardController {

    private final SuperAdminDashboardService superAdminDashboardService;

    @GetMapping
    public SuperAdminDashboardResponse getDashboard() {
        return superAdminDashboardService.getDashboard();
    }
}
