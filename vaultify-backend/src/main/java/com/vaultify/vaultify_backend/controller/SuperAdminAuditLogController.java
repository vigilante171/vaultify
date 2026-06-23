package com.vaultify.vaultify_backend.controller;

import com.vaultify.vaultify_backend.dto.AuditLogResponse;
import com.vaultify.vaultify_backend.dto.PaginatedResponse;
import com.vaultify.vaultify_backend.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SuperAdminAuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/audit-logs")
    public List<AuditLogResponse> getAllAuditLogs() {
        return auditLogService.getAllAuditLogs();
    }

    @GetMapping("/audit-logs/page")
    public PaginatedResponse<AuditLogResponse> getAuditLogsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return auditLogService.getAuditLogsPage(page, size, sortBy, direction);
    }

    @GetMapping("/businesses/{businessId}/audit-logs")
    public List<AuditLogResponse> getBusinessAuditLogs(@PathVariable String businessId) {
        return auditLogService.getBusinessAuditLogs(businessId);
    }
}
