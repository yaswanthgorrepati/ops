package com.ecommerce.ops.repository;

import com.ecommerce.ops.entity.CronJobBatchRetry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CronJobBatchRetryRepository extends JpaRepository<CronJobBatchRetry, Long> {

    @Query("SELECT c FROM CronJobBatchRetry c WHERE c.jobName = :jobName AND c.retryCount < :threshold ORDER BY c.id ASC")
    List<CronJobBatchRetry> findPendingRetryBatches(@Param("jobName") String jobName, @Param("threshold") int threshold, Pageable pageable);
}
