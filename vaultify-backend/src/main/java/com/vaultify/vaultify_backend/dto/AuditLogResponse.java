package com.vaultify.vaultify_backend.dto;

import com.vaultify.vaultify_backend.model.AuditAction;
import com.vaultify.vaultify_backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private String auditLogId;

    private String businessId;

    private String actorUserId;

    private String actorEmail;

    private Role actorRole;

    private AuditAction action;

    private String targetType;

    private String targetId;

    private String description;

    private LocalDateTime createdAt;

    private String message;
}
