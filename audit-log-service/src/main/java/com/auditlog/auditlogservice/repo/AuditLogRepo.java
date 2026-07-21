package com.auditlog.auditlogservice.repo;

import com.auditlog.auditlogservice.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuditLogRepo extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

    boolean existsByEventId(String eventId);
}
