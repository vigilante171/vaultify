package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType;

    private String userId;
    private String businessId;
    private String fullName;
    private String email;
    private Role role;
    private boolean active;
    private String message;
}
