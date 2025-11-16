package com.ecommerce.ops.dto;

import java.util.List;

public class OrderRequestDto {
    List<OrderItemDto> orderItemDtoList;

    public OrderRequestDto() {
    }

    public OrderRequestDto(List<OrderItemDto> orderItemDtoList) {
        this.orderItemDtoList = orderItemDtoList;
    }

    public List<OrderItemDto> getOrderItemDtoList() {
        return orderItemDtoList;
    }

    public void setOrderItemDtoList(List<OrderItemDto> orderItemDtoList) {
        this.orderItemDtoList = orderItemDtoList;
    }
}
