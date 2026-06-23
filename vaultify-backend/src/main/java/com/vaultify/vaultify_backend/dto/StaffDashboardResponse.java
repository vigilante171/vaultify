package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.BusinessType;
import com.vaultify.vaultify_backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDashboardResponse {

    private String staffId;

    private String businessId;

    private String fullName;

    private String email;

    private Role role;

    private boolean active;

    private String businessName;

    private BusinessType businessType;

    private String businessPhone;

    private String businessAddress;

    private String message;
}
