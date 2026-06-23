package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.StaffDashboardResponse;
import com.vaultify.vaultify_backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/me")
    public StaffDashboardResponse getMe() {
        return staffService.getMyDashboard();
    }

    @GetMapping("/dashboard")
    public StaffDashboardResponse getDashboard() {
        return staffService.getMyDashboard();
    }
}
