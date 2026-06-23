package com.vaultify.vaultify_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "businesses")
public class Business {

    @Id
    private String id;

    private String name;

    private BusinessType type;

    private String ownerName;

    private String phone;

    private String address;

    private String logoUrl;

    private Plan plan;

    private SubscriptionStatus subscriptionStatus;

    private LocalDateTime subscriptionStartDate;

    private LocalDateTime subscriptionEndDate;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
