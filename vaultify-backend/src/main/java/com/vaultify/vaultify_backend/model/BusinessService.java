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
@Document(collection = "services")
public class BusinessService {

    @Id
    private String id;

    private String businessId;

    private String name;

    private String description;

    private ServiceCategory category;

    private double price;

    private int durationMinutes;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
