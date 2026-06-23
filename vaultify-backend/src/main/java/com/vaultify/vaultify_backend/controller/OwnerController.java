package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.OwnerDashboardResponse;
import com.vaultify.vaultify_backend.dto.UpdateBusinessProfileRequest;
import com.vaultify.vaultify_backend.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping("/dashboard")
    public OwnerDashboardResponse getDashboard() {
        return ownerService.getMyBusinessDashboard();
    }

    @GetMapping("/my-business")
    public OwnerDashboardResponse getMyBusiness() {
        return ownerService.getMyBusinessDashboard();
    }

    @PutMapping("/my-business")
    public OwnerDashboardResponse updateMyBusiness(
            @Valid @RequestBody UpdateBusinessProfileRequest request
    ) {
        return ownerService.updateMyBusinessProfile(request);
    }
}
