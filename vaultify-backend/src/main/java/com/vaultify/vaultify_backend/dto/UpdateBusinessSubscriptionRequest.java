package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.Plan;
import com.vaultify.vaultify_backend.model.SubscriptionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateBusinessSubscriptionRequest {

    @NotNull(message = "Plan is required")
    private Plan plan;

    @NotNull(message = "Subscription status is required")
    private SubscriptionStatus subscriptionStatus;

    @NotNull(message = "Subscription end date is required")
    private LocalDateTime subscriptionEndDate;
}
