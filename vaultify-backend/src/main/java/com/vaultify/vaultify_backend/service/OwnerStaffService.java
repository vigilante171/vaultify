package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.CreateStaffRequest;
import com.vaultify.vaultify_backend.dto.StaffResponse;
import com.vaultify.vaultify_backend.dto.UpdateStaffRequest;
import com.vaultify.vaultify_backend.model.AuditAction;
import com.vaultify.vaultify_backend.model.Role;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerStaffService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public StaffResponse createStaff(CreateStaffRequest request) {
        User currentOwner = getCurrentOwner();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        User staff = User.builder()
                .businessId(currentOwner.getBusinessId())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STAFF)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User savedStaff = userRepository.save(staff);

        auditLogService.record(
                currentOwner.getBusinessId(),
                currentOwner,
                AuditAction.CREATE_STAFF,
                "USER",
                savedStaff.getId(),
                "Created staff member " + savedStaff.getEmail()
        );

        return mapToResponse(savedStaff, "Staff member created successfully");
    }

    public List<StaffResponse> getMyStaff() {
        User currentOwner = getCurrentOwner();

        return userRepository.findByBusinessIdAndRole(currentOwner.getBusinessId(), Role.STAFF)
                .stream()
                .map(staff -> mapToResponse(staff, "Staff member loaded successfully"))
                .toList();
    }

    public StaffResponse getStaffById(String staffId) {
        User currentOwner = getCurrentOwner();

        User staff = userRepository.findByIdAndBusinessId(staffId, currentOwner.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Staff member not found"));

        if (staff.getRole() != Role.STAFF) {
            throw new RuntimeException("User is not a staff member");
        }

        return mapToResponse(staff, "Staff member loaded successfully");
    }

    public StaffResponse updateStaff(String staffId, UpdateStaffRequest request) {
        User currentOwner = getCurrentOwner();

        User staff = userRepository.findByIdAndBusinessId(staffId, currentOwner.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Staff member not found"));

        if (staff.getRole() != Role.STAFF) {
            throw new RuntimeException("User is not a staff member");
        }

        if (!staff.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        staff.setFullName(request.getFullName());
        staff.setEmail(request.getEmail());
        staff.setActive(request.isActive());
        staff.setUpdatedAt(LocalDateTime.now());

        User updatedStaff = userRepository.save(staff);

        auditLogService.record(
                currentOwner.getBusinessId(),
                currentOwner,
                AuditAction.UPDATE_STAFF,
                "USER",
                updatedStaff.getId(),
                "Updated staff member " + updatedStaff.getEmail()
        );

        return mapToResponse(updatedStaff, "Staff member updated successfully");
    }

    public StaffResponse deleteStaff(String staffId) {
        User currentOwner = getCurrentOwner();

        User staff = userRepository.findByIdAndBusinessId(staffId, currentOwner.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Staff member not found"));

        if (staff.getRole() != Role.STAFF) {
            throw new RuntimeException("User is not a staff member");
        }

        staff.setActive(false);
        staff.setUpdatedAt(LocalDateTime.now());

        User disabledStaff = userRepository.save(staff);

        auditLogService.record(
                currentOwner.getBusinessId(),
                currentOwner,
                AuditAction.DISABLE_STAFF,
                "USER",
                disabledStaff.getId(),
                "Disabled staff member " + disabledStaff.getEmail()
        );

        return mapToResponse(disabledStaff, "Staff member disabled successfully");
    }

    private User getCurrentOwner() {
        return currentUserService.getCurrentOwnerWithActiveBusiness();
    }

    private StaffResponse mapToResponse(User staff, String message) {
        return StaffResponse.builder()
                .staffId(staff.getId())
                .businessId(staff.getBusinessId())
                .fullName(staff.getFullName())
                .email(staff.getEmail())
                .role(staff.getRole())
                .active(staff.isActive())
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .message(message)
                .build();
    }
}
