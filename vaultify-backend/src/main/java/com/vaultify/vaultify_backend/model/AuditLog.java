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
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    private String businessId;

    private String actorUserId;

    private String actorEmail;

    private Role actorRole;

    private AuditAction action;

    private String targetType;

    private String targetId;

    private String description;

    private LocalDateTime createdAt;
}
