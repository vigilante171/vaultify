package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.BusinessResponse;
import com.vaultify.vaultify_backend.dto.CreateBusinessRequest;
import com.vaultify.vaultify_backend.dto.PaginatedResponse;
import com.vaultify.vaultify_backend.dto.UpdateBusinessStatusRequest;
import com.vaultify.vaultify_backend.dto.UpdateBusinessSubscriptionRequest;
import com.vaultify.vaultify_backend.model.SubscriptionStatus;
import com.vaultify.vaultify_backend.service.SuperAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @PostMapping("/businesses")
    public BusinessResponse createBusiness(@Valid @RequestBody CreateBusinessRequest request) {
        return superAdminService.createBusiness(request);
    }

    @GetMapping("/businesses")
    public List<BusinessResponse> getAllBusinesses() {
        return superAdminService.getAllBusinesses();
    }

    @GetMapping("/businesses/page")
    public PaginatedResponse<BusinessResponse> getBusinessesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return superAdminService.getBusinessesPage(page, size, sortBy, direction);
    }

    @GetMapping("/businesses/search")
    public List<BusinessResponse> searchBusinesses(@RequestParam String query) {
        return superAdminService.searchBusinesses(query);
    }

    @GetMapping("/businesses/status/{status}")
    public List<BusinessResponse> getBusinessesByStatus(@PathVariable SubscriptionStatus status) {
        return superAdminService.getBusinessesBySubscriptionStatus(status);
    }

    @GetMapping("/businesses/{businessId}")
    public BusinessResponse getBusinessById(@PathVariable String businessId) {
        return superAdminService.getBusinessById(businessId);
    }

    @PutMapping("/businesses/{businessId}/subscription")
    public BusinessResponse updateBusinessSubscription(
            @PathVariable String businessId,
            @Valid @RequestBody UpdateBusinessSubscriptionRequest request
    ) {
        return superAdminService.updateBusinessSubscription(businessId, request);
    }

    @PutMapping("/businesses/{businessId}/status")
    public BusinessResponse updateBusinessStatus(
            @PathVariable String businessId,
            @RequestBody UpdateBusinessStatusRequest request
    ) {
        return superAdminService.updateBusinessStatus(businessId, request);
    }
}
