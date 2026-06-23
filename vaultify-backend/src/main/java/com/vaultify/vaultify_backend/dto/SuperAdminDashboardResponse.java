package com.vaultify.vaultify_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminDashboardResponse {

    private long totalBusinesses;

    private long activeBusinesses;

    private long inactiveBusinesses;

    private long trialBusinesses;

    private long activeSubscriptions;

    private long expiredSubscriptions;

    private long suspendedSubscriptions;

    private long totalOwners;

    private long totalStaff;

    private String message;
}
