package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.BusinessType;
import com.vaultify.vaultify_backend.model.Plan;
import com.vaultify.vaultify_backend.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessResponse {

    private String businessId;
    private String businessName;
    private BusinessType businessType;

    private String ownerId;
    private String ownerName;
    private String ownerEmail;

    private String phone;
    private String address;

    private Plan plan;
    private SubscriptionStatus subscriptionStatus;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;

    private boolean active;
    private String message;
}
