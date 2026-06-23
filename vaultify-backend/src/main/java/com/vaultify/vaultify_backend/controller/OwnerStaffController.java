package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.CreateStaffRequest;
import com.vaultify.vaultify_backend.dto.StaffResponse;
import com.vaultify.vaultify_backend.dto.UpdateStaffRequest;
import com.vaultify.vaultify_backend.service.OwnerStaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OwnerStaffController {

    private final OwnerStaffService ownerStaffService;

    @PostMapping
    public StaffResponse createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return ownerStaffService.createStaff(request);
    }

    @GetMapping
    public List<StaffResponse> getMyStaff() {
        return ownerStaffService.getMyStaff();
    }

    @GetMapping("/{staffId}")
    public StaffResponse getStaffById(@PathVariable String staffId) {
        return ownerStaffService.getStaffById(staffId);
    }

    @PutMapping("/{staffId}")
    public StaffResponse updateStaff(
            @PathVariable String staffId,
            @Valid @RequestBody UpdateStaffRequest request
    ) {
        return ownerStaffService.updateStaff(staffId, request);
    }

    @DeleteMapping("/{staffId}")
    public StaffResponse deleteStaff(@PathVariable String staffId) {
        return ownerStaffService.deleteStaff(staffId);
    }
}
