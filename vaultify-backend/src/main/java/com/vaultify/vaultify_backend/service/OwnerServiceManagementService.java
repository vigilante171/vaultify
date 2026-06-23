package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.CreateServiceRequest;
import com.vaultify.vaultify_backend.dto.ServiceResponse;
import com.vaultify.vaultify_backend.dto.UpdateServiceRequest;
import com.vaultify.vaultify_backend.model.AuditAction;
import com.vaultify.vaultify_backend.model.BusinessService;
import com.vaultify.vaultify_backend.model.ServiceCategory;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerServiceManagementService {

    private final CurrentUserService currentUserService;
    private final ServiceRepository serviceRepository;
    private final AuditLogService auditLogService;

    public ServiceResponse createService(CreateServiceRequest request) {
        User currentUser = getCurrentOwner();

        if (serviceRepository.existsByBusinessIdAndNameIgnoreCase(currentUser.getBusinessId(), request.getName())) {
            throw new RuntimeException("Service already exists for this business");
        }

        LocalDateTime now = LocalDateTime.now();

        BusinessService service = BusinessService.builder()
                .businessId(currentUser.getBusinessId())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .durationMinutes(request.getDurationMinutes())
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        BusinessService savedService = serviceRepository.save(service);

        auditLogService.record(
                currentUser.getBusinessId(),
                currentUser,
                AuditAction.CREATE_SERVICE,
                "SERVICE",
                savedService.getId(),
                "Created service " + savedService.getName()
        );

        return mapToResponse(savedService, "Service created successfully");
    }

    public List<ServiceResponse> getMyServices() {
        User currentUser = getCurrentOwner();

        return serviceRepository.findByBusinessId(currentUser.getBusinessId())
                .stream()
                .map(service -> mapToResponse(service, "Service loaded successfully"))
                .toList();
    }

    public List<ServiceResponse> searchServices(String query) {
        User currentUser = getCurrentOwner();

        if (query == null || query.isBlank()) {
            return getMyServices();
        }

        return serviceRepository.findByBusinessIdAndNameContainingIgnoreCase(
                        currentUser.getBusinessId(),
                        query.trim()
                )
                .stream()
                .map(service -> mapToResponse(service, "Service search result loaded successfully"))
                .toList();
    }

    public List<ServiceResponse> getServicesByCategory(ServiceCategory category) {
        User currentUser = getCurrentOwner();

        return serviceRepository.findByBusinessIdAndCategory(currentUser.getBusinessId(), category)
                .stream()
                .map(service -> mapToResponse(service, "Service category result loaded successfully"))
                .toList();
    }

    public ServiceResponse updateService(String serviceId, UpdateServiceRequest request) {
        User currentUser = getCurrentOwner();

        BusinessService service = serviceRepository.findByIdAndBusinessId(serviceId, currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setCategory(request.getCategory());
        service.setPrice(request.getPrice());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setActive(request.isActive());
        service.setUpdatedAt(LocalDateTime.now());

        BusinessService updatedService = serviceRepository.save(service);

        auditLogService.record(
                currentUser.getBusinessId(),
                currentUser,
                AuditAction.UPDATE_SERVICE,
                "SERVICE",
                updatedService.getId(),
                "Updated service " + updatedService.getName()
        );

        return mapToResponse(updatedService, "Service updated successfully");
    }

    public ServiceResponse deleteService(String serviceId) {
        User currentUser = getCurrentOwner();

        BusinessService service = serviceRepository.findByIdAndBusinessId(serviceId, currentUser.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setActive(false);
        service.setUpdatedAt(LocalDateTime.now());

        BusinessService disabledService = serviceRepository.save(service);

        auditLogService.record(
                currentUser.getBusinessId(),
                currentUser,
                AuditAction.DISABLE_SERVICE,
                "SERVICE",
                disabledService.getId(),
                "Disabled service " + disabledService.getName()
        );

        return mapToResponse(disabledService, "Service disabled successfully");
    }

    private User getCurrentOwner() {
        return currentUserService.getCurrentOwnerWithActiveBusiness();
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
