package com.vaultify.vaultify_backend.repository;

import com.vaultify.vaultify_backend.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

    List<AuditLog> findByBusinessId(String businessId);

    List<AuditLog> findByActorUserId(String actorUserId);
}
