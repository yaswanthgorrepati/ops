package com.ecommerce.ops.repository;

import com.ecommerce.ops.entity.CronJobLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CronJobLogRepository extends JpaRepository<CronJobLogs, Long> {

    Optional<CronJobLogs> findTopByJobNameOrderByEndIndexDesc(String jobName);

    @Query("SELECT c FROM CronJobLogs c WHERE c.jobName = :jobName AND :orderId BETWEEN c.startIndex AND c.endIndex")
    Optional<CronJobLogs> findByJobNameAndOrderIdInRange(@Param("jobName") String jobName, @Param("orderId") Long orderId);
}
