package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.OwnerProfileResponse;
import com.vaultify.vaultify_backend.dto.UpdateOwnerProfileRequest;
import com.vaultify.vaultify_backend.dto.UpdatePasswordRequest;
import com.vaultify.vaultify_backend.model.AuditAction;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OwnerProfileService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public OwnerProfileResponse getMyProfile() {
        User owner = currentUserService.getCurrentOwnerWithActiveBusiness();

        return mapToResponse(owner, "Owner profile loaded successfully");
    }

    public OwnerProfileResponse updateMyProfile(UpdateOwnerProfileRequest request) {
        User owner = currentUserService.getCurrentOwnerWithActiveBusiness();

        if (!owner.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        owner.setFullName(request.getFullName());
        owner.setEmail(request.getEmail());
        owner.setUpdatedAt(LocalDateTime.now());

        User updatedOwner = userRepository.save(owner);

        auditLogService.record(
                updatedOwner.getBusinessId(),
                updatedOwner,
                AuditAction.UPDATE_PROFILE,
                "USER",
                updatedOwner.getId(),
                "Owner updated profile"
        );

        return mapToResponse(updatedOwner, "Owner profile updated successfully");
    }

    public OwnerProfileResponse updateMyPassword(UpdatePasswordRequest request) {
        User owner = currentUserService.getCurrentOwnerWithActiveBusiness();

        if (!passwordEncoder.matches(request.getCurrentPassword(), owner.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        owner.setPassword(passwordEncoder.encode(request.getNewPassword()));
        owner.setUpdatedAt(LocalDateTime.now());

        User updatedOwner = userRepository.save(owner);

        auditLogService.record(
                updatedOwner.getBusinessId(),
                updatedOwner,
                AuditAction.UPDATE_PASSWORD,
                "USER",
                updatedOwner.getId(),
                "Owner updated password"
        );

        return mapToResponse(updatedOwner, "Password updated successfully");
    }

    private OwnerProfileResponse mapToResponse(User owner, String message) {
        return OwnerProfileResponse.builder()
                .ownerId(owner.getId())
                .businessId(owner.getBusinessId())
                .fullName(owner.getFullName())
                .email(owner.getEmail())
                .role(owner.getRole())
                .active(owner.isActive())
                .createdAt(owner.getCreatedAt())
                .updatedAt(owner.getUpdatedAt())
                .message(message)
                .build();
    }
}
