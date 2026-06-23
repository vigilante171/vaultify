package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.SuperAdminProfileResponse;
import com.vaultify.vaultify_backend.dto.UpdatePasswordRequest;
import com.vaultify.vaultify_backend.dto.UpdateSuperAdminProfileRequest;
import com.vaultify.vaultify_backend.service.SuperAdminProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SuperAdminProfileController {

    private final SuperAdminProfileService superAdminProfileService;

    @GetMapping
    public SuperAdminProfileResponse getMyProfile() {
        return superAdminProfileService.getMyProfile();
    }

    @PutMapping
    public SuperAdminProfileResponse updateMyProfile(
            @Valid @RequestBody UpdateSuperAdminProfileRequest request
    ) {
        return superAdminProfileService.updateMyProfile(request);
    }

    @PutMapping("/password")
    public SuperAdminProfileResponse updateMyPassword(
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        return superAdminProfileService.updateMyPassword(request);
    }
}
