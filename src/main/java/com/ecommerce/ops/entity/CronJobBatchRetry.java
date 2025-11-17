package com.ecommerce.ops.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cron_job_batch_retry")
public class CronJobBatchRetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long startIndex;
    private Long endIndex;

    private String jobName;

    private Integer retryCount;

    @Column(nullable = false, updatable = false, insertable = false)
    private LocalDateTime startTime;

    @Column(nullable = false, updatable = false, insertable = false)
    private LocalDateTime endTime;

    public CronJobBatchRetry() {
    }

    public CronJobBatchRetry(Long startIndex, Long endIndex, String jobName) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.jobName = jobName;
        retryCount = 1;
    }

    public Long getId() {
        return id;
    }

    public Long getStartIndex() {
        return startIndex;
    }

    public Long getEndIndex() {
        return endIndex;
    }

    public String getJobName() {
        return jobName;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

}
