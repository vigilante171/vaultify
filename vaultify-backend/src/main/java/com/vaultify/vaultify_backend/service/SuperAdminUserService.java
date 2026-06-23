package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.PaginatedResponse;
import com.vaultify.vaultify_backend.dto.SuperAdminUserResponse;
import com.vaultify.vaultify_backend.dto.UpdateUserStatusRequest;
import com.vaultify.vaultify_backend.model.Business;
import com.vaultify.vaultify_backend.model.Role;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.BusinessRepository;
import com.vaultify.vaultify_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuperAdminUserService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public List<SuperAdminUserResponse> getAllUsers() {
        validateSuperAdmin();

        return userRepository.findAll()
                .stream()
                .map(user -> mapToResponse(user, "User loaded successfully"))
                .toList();
    }

    public PaginatedResponse<SuperAdminUserResponse> getUsersPage(
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        validateSuperAdmin();

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size, sort));

        List<SuperAdminUserResponse> content = userPage.getContent()
                .stream()
                .map(user -> mapToResponse(user, "User loaded successfully"))
                .toList();

        return PaginatedResponse.<SuperAdminUserResponse>builder()
                .content(content)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .message("Users page loaded successfully")
                .build();
    }

    public List<SuperAdminUserResponse> getUsersByRole(Role role) {
        validateSuperAdmin();

        return userRepository.findByRole(role)
                .stream()
                .map(user -> mapToResponse(user, "User role result loaded successfully"))
                .toList();
    }

    public SuperAdminUserResponse getUserById(String userId) {
        validateSuperAdmin();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user, "User loaded successfully");
    }

    public List<SuperAdminUserResponse> getUsersByBusiness(String businessId) {
        validateSuperAdmin();

        businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        return userRepository.findByBusinessId(businessId)
                .stream()
                .map(user -> mapToResponse(user, "Business user loaded successfully"))
                .toList();
    }

    public SuperAdminUserResponse updateUserStatus(String userId, UpdateUserStatusRequest request) {
        User currentSuperAdmin = validateSuperAdmin();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.SUPER_ADMIN && user.getId().equals(currentSuperAdmin.getId())) {
            throw new RuntimeException("You cannot disable your own super admin account");
        }

        user.setActive(request.isActive());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser, "User status updated successfully");
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

    private SuperAdminUserResponse mapToResponse(User user, String message) {
        String businessName = null;

        if (user.getBusinessId() != null) {
            businessName = businessRepository.findById(user.getBusinessId())
                    .map(Business::getName)
                    .orElse(null);
        }

        return SuperAdminUserResponse.builder()
                .userId(user.getId())
                .businessId(user.getBusinessId())
                .businessName(businessName)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .message(message)
                .build();
    }
}
