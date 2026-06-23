package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.BusinessResponse;
import com.vaultify.vaultify_backend.dto.CreateBusinessRequest;
import com.vaultify.vaultify_backend.dto.PaginatedResponse;
import com.vaultify.vaultify_backend.dto.UpdateBusinessStatusRequest;
import com.vaultify.vaultify_backend.dto.UpdateBusinessSubscriptionRequest;
import com.vaultify.vaultify_backend.model.AuditAction;
import com.vaultify.vaultify_backend.model.Business;
import com.vaultify.vaultify_backend.model.Role;
import com.vaultify.vaultify_backend.model.SubscriptionStatus;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.BusinessRepository;
import com.vaultify.vaultify_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final AuditLogService auditLogService;

    public BusinessResponse createBusiness(CreateBusinessRequest request) {
        User actor = currentUserService.getCurrentUser();

        if (userRepository.existsByEmail(request.getOwnerEmail())) {
            throw new RuntimeException("Owner email already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        Business business = Business.builder()
                .name(request.getBusinessName())
                .type(request.getBusinessType())
                .ownerName(request.getOwnerName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .logoUrl(null)
                .plan(request.getPlan())
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .subscriptionStartDate(now)
                .subscriptionEndDate(now.plusMonths(1))
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Business savedBusiness = businessRepository.save(business);

        User owner = User.builder()
                .businessId(savedBusiness.getId())
                .fullName(request.getOwnerName())
                .email(request.getOwnerEmail())
                .password(passwordEncoder.encode(request.getOwnerPassword()))
                .role(Role.OWNER)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User savedOwner = userRepository.save(owner);

        auditLogService.record(
                savedBusiness.getId(),
                actor,
                AuditAction.CREATE_BUSINESS,
                "BUSINESS",
                savedBusiness.getId(),
                "Created business " + savedBusiness.getName() + " with owner " + savedOwner.getEmail()
        );

        return mapToBusinessResponse(savedBusiness, savedOwner, "Business and owner account created successfully");
    }

    public List<BusinessResponse> getAllBusinesses() {
        return businessRepository.findAll()
                .stream()
                .map(business -> mapToBusinessResponse(business, "Business loaded successfully"))
                .toList();
    }

    public PaginatedResponse<BusinessResponse> getBusinessesPage(
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        validateSuperAdmin();

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Page<Business> businessPage = businessRepository.findAll(PageRequest.of(page, size, sort));

        List<BusinessResponse> content = businessPage.getContent()
                .stream()
                .map(business -> mapToBusinessResponse(business, "Business loaded successfully"))
                .toList();

        return PaginatedResponse.<BusinessResponse>builder()
                .content(content)
                .page(businessPage.getNumber())
                .size(businessPage.getSize())
                .totalElements(businessPage.getTotalElements())
                .totalPages(businessPage.getTotalPages())
                .first(businessPage.isFirst())
                .last(businessPage.isLast())
                .message("Businesses page loaded successfully")
                .build();
    }

    public List<BusinessResponse> searchBusinesses(String query) {
        validateSuperAdmin();

        if (query == null || query.isBlank()) {
            return getAllBusinesses();
        }

        return businessRepository.findByNameContainingIgnoreCase(query.trim())
                .stream()
                .map(business -> mapToBusinessResponse(business, "Business search result loaded successfully"))
                .toList();
    }

    public List<BusinessResponse> getBusinessesBySubscriptionStatus(SubscriptionStatus status) {
        validateSuperAdmin();

        return businessRepository.findBySubscriptionStatus(status)
                .stream()
                .map(business -> mapToBusinessResponse(business, "Business status result loaded successfully"))
                .toList();
    }

    public BusinessResponse getBusinessById(String businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        return mapToBusinessResponse(business, "Business loaded successfully");
    }

    public BusinessResponse updateBusinessSubscription(
            String businessId,
            UpdateBusinessSubscriptionRequest request
    ) {
        User actor = currentUserService.getCurrentUser();

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        business.setPlan(request.getPlan());
        business.setSubscriptionStatus(request.getSubscriptionStatus());
        business.setSubscriptionEndDate(request.getSubscriptionEndDate());
        business.setUpdatedAt(LocalDateTime.now());

        Business updatedBusiness = businessRepository.save(business);

        auditLogService.record(
                updatedBusiness.getId(),
                actor,
                AuditAction.UPDATE_BUSINESS_SUBSCRIPTION,
                "BUSINESS",
                updatedBusiness.getId(),
                "Updated subscription for business " + updatedBusiness.getName()
        );

        return mapToBusinessResponse(updatedBusiness, "Business subscription updated successfully");
    }

    public BusinessResponse updateBusinessStatus(
            String businessId,
            UpdateBusinessStatusRequest request
    ) {
        User actor = currentUserService.getCurrentUser();

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        business.setActive(request.isActive());
        business.setUpdatedAt(LocalDateTime.now());

        Business updatedBusiness = businessRepository.save(business);

        userRepository.findByBusinessId(businessId).forEach(user -> {
            user.setActive(request.isActive());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        });

        auditLogService.record(
                updatedBusiness.getId(),
                actor,
                AuditAction.UPDATE_BUSINESS_STATUS,
                "BUSINESS",
                updatedBusiness.getId(),
                "Updated business status for " + updatedBusiness.getName() + " to active=" + updatedBusiness.isActive()
        );

        return mapToBusinessResponse(updatedBusiness, "Business status updated successfully");
    }

    private User validateSuperAdmin() {
        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() != Role.SUPER_ADMIN) {
            throw new RuntimeException("Only super admin can access this resource");
        }

        if (!currentUser.isActive()) {
            throw new RuntimeException("Super admin account is disabled");
        }

        return currentUser;
    }

    private BusinessResponse mapToBusinessResponse(Business business, String message) {
        User owner = userRepository.findByBusinessId(business.getId())
                .stream()
                .filter(user -> user.getRole() == Role.OWNER)
                .findFirst()
                .orElse(null);

        return mapToBusinessResponse(business, owner, message);
    }

    private BusinessResponse mapToBusinessResponse(Business business, User owner, String message) {
        return BusinessResponse.builder()
                .businessId(business.getId())
                .businessName(business.getName())
                .businessType(business.getType())
                .ownerId(owner != null ? owner.getId() : null)
                .ownerName(owner != null ? owner.getFullName() : business.getOwnerName())
                .ownerEmail(owner != null ? owner.getEmail() : null)
                .phone(business.getPhone())
                .address(business.getAddress())
                .plan(business.getPlan())
                .subscriptionStatus(business.getSubscriptionStatus())
                .subscriptionStartDate(business.getSubscriptionStartDate())
                .subscriptionEndDate(business.getSubscriptionEndDate())
                .active(business.isActive())
                .message(message)
                .build();
    }
}
