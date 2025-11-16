package com.ecommerce.ops.dto;

import com.ecommerce.ops.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto extends OrderDTO {

    List<OrderItemDto> orderItemDtoList;

    public OrderResponseDto(Long orderId, Long userId, OrderStatus orderStatus) {
        super(orderId, userId, orderStatus, null);
    }

    public OrderResponseDto(Long orderId, Long userId, OrderStatus orderStatus, List<OrderItemDto> orderItemDtoList) {
        super(orderId, userId, orderStatus, null);
        this.orderItemDtoList = orderItemDtoList;
    }

    public List<OrderItemDto> getOrderItemDtoList() {
        return orderItemDtoList;
    }

    public void setOrderItemDtoList(List<OrderItemDto> orderItemDtoList) {
        this.orderItemDtoList = orderItemDtoList;
    }
}
