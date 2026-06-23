package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.SuperAdminProfileResponse;
import com.vaultify.vaultify_backend.dto.UpdatePasswordRequest;
import com.vaultify.vaultify_backend.dto.UpdateSuperAdminProfileRequest;
import com.vaultify.vaultify_backend.model.AuditAction;
import com.vaultify.vaultify_backend.model.Role;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SuperAdminProfileService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public SuperAdminProfileResponse getMyProfile() {
        User superAdmin = getCurrentSuperAdmin();

        return mapToResponse(superAdmin, "Super admin profile loaded successfully");
    }

    public SuperAdminProfileResponse updateMyProfile(UpdateSuperAdminProfileRequest request) {
        User superAdmin = getCurrentSuperAdmin();

        if (!superAdmin.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        superAdmin.setFullName(request.getFullName());
        superAdmin.setEmail(request.getEmail());
        superAdmin.setUpdatedAt(LocalDateTime.now());

        User updatedSuperAdmin = userRepository.save(superAdmin);

        auditLogService.record(
                null,
                updatedSuperAdmin,
                AuditAction.UPDATE_PROFILE,
                "USER",
                updatedSuperAdmin.getId(),
                "Super admin updated profile"
        );

        return mapToResponse(updatedSuperAdmin, "Super admin profile updated successfully");
    }

    public SuperAdminProfileResponse updateMyPassword(UpdatePasswordRequest request) {
        User superAdmin = getCurrentSuperAdmin();

        if (!passwordEncoder.matches(request.getCurrentPassword(), superAdmin.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        superAdmin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        superAdmin.setUpdatedAt(LocalDateTime.now());

        User updatedSuperAdmin = userRepository.save(superAdmin);

        auditLogService.record(
                null,
                updatedSuperAdmin,
                AuditAction.UPDATE_PASSWORD,
                "USER",
                updatedSuperAdmin.getId(),
                "Super admin updated password"
        );

        return mapToResponse(updatedSuperAdmin, "Password updated successfully");
    }

    private User getCurrentSuperAdmin() {
        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() != Role.SUPER_ADMIN) {
            throw new RuntimeException("Only super admin can access this resource");
        }

        if (!currentUser.isActive()) {
            throw new RuntimeException("Super admin account is disabled");
        }

        return currentUser;
    }

    private SuperAdminProfileResponse mapToResponse(User superAdmin, String message) {
        return SuperAdminProfileResponse.builder()
                .superAdminId(superAdmin.getId())
                .fullName(superAdmin.getFullName())
                .email(superAdmin.getEmail())
                .role(superAdmin.getRole())
                .active(superAdmin.isActive())
                .createdAt(superAdmin.getCreatedAt())
                .updatedAt(superAdmin.getUpdatedAt())
                .message(message)
                .build();
    }
}
