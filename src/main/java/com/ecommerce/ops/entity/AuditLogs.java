package com.ecommerce.ops.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tableName;

    @Column(columnDefinition = "TEXT")
    private String previousData;

    @Column(columnDefinition = "TEXT")
    private String currentData;

    @Column(nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    public AuditLogs() {
    }

    public AuditLogs(String tableName, String previousData, String currentData) {
        this.tableName = tableName;
        this.previousData = previousData;
        this.currentData = currentData;
    }

    public Long getId() {
        return id;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPreviousData() {
        return previousData;
    }

    public String getCurrentData() {
        return currentData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
