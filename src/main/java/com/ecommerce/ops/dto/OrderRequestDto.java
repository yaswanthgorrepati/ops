package com.ecommerce.ops.dto;

import com.ecommerce.ops.enums.OrderStatus;

import java.util.List;

public class OrderRequestDto {
    private OrderStatus orderStatus;
    List<OrderItemDto> orderItemDtoList;

    public OrderRequestDto() {
    }

    public OrderRequestDto(OrderStatus orderStatus, List<OrderItemDto> orderItemDtoList) {
        this.orderStatus = orderStatus;
        this.orderItemDtoList = orderItemDtoList;
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
