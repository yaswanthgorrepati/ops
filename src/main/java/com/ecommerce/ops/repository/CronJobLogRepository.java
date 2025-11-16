package com.ecommerce.ops.repository;

import com.ecommerce.ops.entity.CronJobLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobLogRepository extends JpaRepository<CronJobLogs, Long> {
}
