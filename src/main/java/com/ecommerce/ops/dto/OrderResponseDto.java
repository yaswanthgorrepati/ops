package com.ecommerce.ops.dto;

import com.ecommerce.ops.enums.OrderStatus;

import java.util.List;

public class OrderResponseDto {

    private Long orderId;
    private Long userId;
    private OrderStatus orderStatus;
    List<OrderItemDto> orderItemDtoList;

    public OrderResponseDto(Long orderId, Long userId, OrderStatus orderStatus, List<OrderItemDto> orderItemDtoList) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.orderItemDtoList = orderItemDtoList;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderItemDto> getOrderItemDtoList() {
        return orderItemDtoList;
    }

    public void setOrderItemDtoList(List<OrderItemDto> orderItemDtoList) {
        this.orderItemDtoList = orderItemDtoList;
    }
}
