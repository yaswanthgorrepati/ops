package com.ecommerce.ops.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cron_job_logs")
public class CronJobLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobName;
    private Integer totalOrdersProcessed;
    private Long successCount;
    private Long failureCount;

    @Column(nullable = false, updatable = false, insertable = false)
    private LocalDateTime startTime;

    @Column(nullable = false, updatable = false, insertable = false)
    private LocalDateTime endTime;

    public CronJobLogs() {
    }

    public CronJobLogs(String jobName) {
        this.jobName = jobName;
    }

    public Long getId() {
        return id;
    }

    public Integer getTotalOrdersProcessed() {
        return totalOrdersProcessed;
    }

    public void setTotalOrdersProcessed(Integer totalOrdersProcessed) {
        this.totalOrdersProcessed = totalOrdersProcessed;
    }

    public String getJobName() {
        return jobName;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Long failureCount) {
        this.failureCount = failureCount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

}
