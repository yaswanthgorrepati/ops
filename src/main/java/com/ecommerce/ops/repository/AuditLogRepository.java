package com.ecommerce.ops.repository;

import com.ecommerce.ops.entity.AuditLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogs, Long> {
}
