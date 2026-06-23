package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.CreateServiceRequest;
import com.vaultify.vaultify_backend.dto.ServiceResponse;
import com.vaultify.vaultify_backend.dto.UpdateServiceRequest;
import com.vaultify.vaultify_backend.model.ServiceCategory;
import com.vaultify.vaultify_backend.service.OwnerServiceManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OwnerServiceController {

    private final OwnerServiceManagementService ownerServiceManagementService;

    @PostMapping
    public ServiceResponse createService(@Valid @RequestBody CreateServiceRequest request) {
        return ownerServiceManagementService.createService(request);
    }

    @GetMapping
    public List<ServiceResponse> getMyServices() {
        return ownerServiceManagementService.getMyServices();
    }

    @GetMapping("/search")
    public List<ServiceResponse> searchServices(@RequestParam String query) {
        return ownerServiceManagementService.searchServices(query);
    }

    @GetMapping("/category/{category}")
    public List<ServiceResponse> getServicesByCategory(@PathVariable ServiceCategory category) {
        return ownerServiceManagementService.getServicesByCategory(category);
    }

    @PutMapping("/{serviceId}")
    public ServiceResponse updateService(
            @PathVariable String serviceId,
            @Valid @RequestBody UpdateServiceRequest request
    ) {
        return ownerServiceManagementService.updateService(serviceId, request);
    }

    @DeleteMapping("/{serviceId}")
    public ServiceResponse deleteService(@PathVariable String serviceId) {
        return ownerServiceManagementService.deleteService(serviceId);
    }
}
