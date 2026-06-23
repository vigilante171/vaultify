package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {

    private String clientId;

    private String businessId;

    private String fullName;

    private String phone;

    private String email;

    private Gender gender;

    private String notes;

    private int totalVisits;

    private double totalSpent;

    private LocalDateTime lastVisitDate;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String message;
}
