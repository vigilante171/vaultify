package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.ServiceResponse;
import com.vaultify.vaultify_backend.service.StaffServiceAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffServiceController {

    private final StaffServiceAccessService staffServiceAccessService;

    @GetMapping
    public List<ServiceResponse> getMyBusinessServices() {
        return staffServiceAccessService.getMyBusinessServices();
    }

    @GetMapping("/{serviceId}")
    public ServiceResponse getServiceById(@PathVariable String serviceId) {
        return staffServiceAccessService.getServiceById(serviceId);
    }
}
