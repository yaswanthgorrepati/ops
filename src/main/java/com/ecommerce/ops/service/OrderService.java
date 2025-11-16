package com.ecommerce.ops.service;

import com.ecommerce.ops.dto.OrderDTO;
import com.ecommerce.ops.dto.OrderItemDto;
import com.ecommerce.ops.dto.OrderResponseDto;
import com.ecommerce.ops.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(List<OrderItemDto> orderItemDtoList, Long userId);

    OrderResponseDto getOrder(Long orderId, Long userId);

    List<OrderResponseDto> listOrders(Long userId, OrderStatus status);

    OrderDTO cancelOrder(Long orderId, Long userId);

}
