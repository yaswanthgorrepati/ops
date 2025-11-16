package com.ecommerce.ops.service;

import com.ecommerce.ops.dto.OrderCancelDTO;
import com.ecommerce.ops.dto.OrderRequestDto;
import com.ecommerce.ops.dto.OrderResponseDto;
import com.ecommerce.ops.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto orderDto, Long userId);

    OrderResponseDto getOrder(Long orderId, Long userId);

    List<OrderResponseDto> listOrders(Long userId, OrderStatus status);

    OrderCancelDTO cancelOrder(Long orderId, Long userId);

}
