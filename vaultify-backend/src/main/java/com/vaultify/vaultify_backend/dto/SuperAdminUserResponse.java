package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminUserResponse {

    private String userId;

    private String businessId;

    private String businessName;

    private String fullName;

    private String email;

    private Role role;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String message;
}
