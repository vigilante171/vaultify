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
@Document(collection = "clients")
public class Client {

    @Id
    private String id;

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
}
