package com.ecommerce.ops.entity;

import com.ecommerce.ops.enums.OrderStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private Double orderTotal;

    @Column(nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false, insertable = false)
    private LocalDateTime updatedAt;

    public Orders() {
    }

    public Orders(Long customerId, OrderStatus orderStatus, Double orderTotal) {
        this.customerId = customerId;
        this.orderStatus = orderStatus;
        this.orderTotal = orderTotal;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
