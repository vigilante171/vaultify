package com.vaultify.vaultify_backend.service;

import com.vaultify.vaultify_backend.dto.AuditLogResponse;
import com.vaultify.vaultify_backend.dto.PaginatedResponse;
import com.vaultify.vaultify_backend.model.AuditAction;
import com.vaultify.vaultify_backend.model.AuditLog;
import com.vaultify.vaultify_backend.model.Role;
import com.vaultify.vaultify_backend.model.User;
import com.vaultify.vaultify_backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserService currentUserService;

    public void record(
            String businessId,
            User actor,
            AuditAction action,
            String targetType,
            String targetId,
            String description
    ) {
        AuditLog auditLog = AuditLog.builder()
                .businessId(businessId)
                .actorUserId(actor != null ? actor.getId() : null)
                .actorEmail(actor != null ? actor.getEmail() : null)
                .actorRole(actor != null ? actor.getRole() : null)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }

    public List<AuditLogResponse> getAllAuditLogs() {
        validateSuperAdmin();

        return auditLogRepository.findAll()
                .stream()
                .map(log -> mapToResponse(log, "Audit log loaded successfully"))
                .toList();
    }

    public PaginatedResponse<AuditLogResponse> getAuditLogsPage(
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        validateSuperAdmin();

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Page<AuditLog> auditLogPage = auditLogRepository.findAll(PageRequest.of(page, size, sort));

        List<AuditLogResponse> content = auditLogPage.getContent()
                .stream()
                .map(log -> mapToResponse(log, "Audit log loaded successfully"))
                .toList();

        return PaginatedResponse.<AuditLogResponse>builder()
                .content(content)
                .page(auditLogPage.getNumber())
                .size(auditLogPage.getSize())
                .totalElements(auditLogPage.getTotalElements())
                .totalPages(auditLogPage.getTotalPages())
                .first(auditLogPage.isFirst())
                .last(auditLogPage.isLast())
                .message("Audit logs page loaded successfully")
                .build();
    }

    public List<AuditLogResponse> getBusinessAuditLogs(String businessId) {
        validateSuperAdmin();

        return auditLogRepository.findByBusinessId(businessId)
                .stream()
                .map(log -> mapToResponse(log, "Business audit log loaded successfully"))
                .toList();
    }

    private void validateSuperAdmin() {
        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() != Role.SUPER_ADMIN) {
            throw new RuntimeException("Only super admin can access audit logs");
        }

        if (!currentUser.isActive()) {
            throw new RuntimeException("Super admin account is disabled");
        }
    }

    private AuditLogResponse mapToResponse(AuditLog log, String message) {
        return AuditLogResponse.builder()
                .auditLogId(log.getId())
                .businessId(log.getBusinessId())
                .actorUserId(log.getActorUserId())
                .actorEmail(log.getActorEmail())
                .actorRole(log.getActorRole())
                .action(log.getAction())
                .targetType(log.getTargetType())
                .targetId(log.getTargetId())
                .description(log.getDescription())
                .createdAt(log.getCreatedAt())
                .message(message)
                .build();
    }
}
