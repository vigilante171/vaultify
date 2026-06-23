package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.ServiceResponse;
import com.vaultify.vaultify_backend.model.BusinessService;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceAccessService {

    private final CurrentUserService currentUserService;
    private final ServiceRepository serviceRepository;

    public List<ServiceResponse> getMyBusinessServices() {
        User staff = currentUserService.getCurrentStaffWithActiveBusiness();

        return serviceRepository.findByBusinessIdAndActive(staff.getBusinessId(), true)
                .stream()
                .map(service -> mapToResponse(service, "Service loaded successfully"))
                .toList();
    }

    public ServiceResponse getServiceById(String serviceId) {
        User staff = currentUserService.getCurrentStaffWithActiveBusiness();

        BusinessService service = serviceRepository.findByIdAndBusinessId(serviceId, staff.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.isActive()) {
            throw new RuntimeException("Service is inactive");
        }

        return mapToResponse(service, "Service loaded successfully");
    }

    private ServiceResponse mapToResponse(BusinessService service, String message) {
        return ServiceResponse.builder()
                .serviceId(service.getId())
                .businessId(service.getBusinessId())
                .name(service.getName())
                .description(service.getDescription())
                .category(service.getCategory())
                .price(service.getPrice())
                .durationMinutes(service.getDurationMinutes())
                .active(service.isActive())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .message(message)
                .build();
    }
}
