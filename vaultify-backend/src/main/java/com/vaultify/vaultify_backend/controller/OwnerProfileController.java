package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.OwnerProfileResponse;
import com.vaultify.vaultify_backend.dto.UpdateOwnerProfileRequest;
import com.vaultify.vaultify_backend.dto.UpdatePasswordRequest;
import com.vaultify.vaultify_backend.service.OwnerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OwnerProfileController {

    private final OwnerProfileService ownerProfileService;

    @GetMapping
    public OwnerProfileResponse getMyProfile() {
        return ownerProfileService.getMyProfile();
    }

    @PutMapping
    public OwnerProfileResponse updateMyProfile(
            @Valid @RequestBody UpdateOwnerProfileRequest request

    ) {
        return ownerProfileService.updateMyProfile(request);
    }

    @PutMapping("/password")
    public OwnerProfileResponse updateMyPassword(
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        return ownerProfileService.updateMyPassword(request);
    }
}
