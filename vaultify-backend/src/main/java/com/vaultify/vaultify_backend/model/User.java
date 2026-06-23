package com.vaultify.vaultify_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String businessId;

    private String fullName;

    @Indexed(unique = true)
    private String email;

    private String password;

    private Role role;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
