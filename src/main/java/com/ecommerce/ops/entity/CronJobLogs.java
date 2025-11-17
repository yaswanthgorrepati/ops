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

    private Integer totalOrdersCount;
    private Integer successCount;
    private Integer failureCount;

    private Long startIndex;
    private Long endIndex;

    @Column(nullable = false, updatable = false, insertable = false)
    private LocalDateTime startTime;

    @Column(nullable = false, updatable = false, insertable = false)
    private LocalDateTime endTime;

    public CronJobLogs() {
    }

    public CronJobLogs(String jobName) {
        this.jobName = jobName;
    }

    public CronJobLogs(String jobName, Integer totalOrdersCount, Integer successCount, Integer failureCount, Long startIndex, Long endIndex) {
        this.jobName = jobName;
        this.totalOrdersCount = totalOrdersCount;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getTotalOrdersCount() {
        return totalOrdersCount;
    }

    public void setTotalOrdersCount(Integer totalOrdersCount) {
        this.totalOrdersCount = totalOrdersCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public Long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Long startIndex) {
        this.startIndex = startIndex;
    }

    public Long getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Long endIndex) {
        this.endIndex = endIndex;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
