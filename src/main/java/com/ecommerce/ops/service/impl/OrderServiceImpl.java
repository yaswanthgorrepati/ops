package com.ecommerce.ops.service.impl;

import com.ecommerce.ops.dto.OrderDTO;
import com.ecommerce.ops.dto.OrderItemDto;
import com.ecommerce.ops.dto.OrderRequestDto;
import com.ecommerce.ops.dto.OrderResponseDto;
import com.ecommerce.ops.entity.OrderItems;
import com.ecommerce.ops.entity.Orders;
import com.ecommerce.ops.enums.OrderStatus;
import com.ecommerce.ops.repository.OrderItemRepository;
import com.ecommerce.ops.repository.OrderRepository;
import com.ecommerce.ops.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ecommerce.ops.utils.Constants.ORDER_CANCELLATION_SUCCESS_MESSAGE;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderDto, Long userId) {
        try {

            if (Objects.isNull(orderDto) || CollectionUtils.isEmpty(orderDto.getOrderItemDtoList())) {
                //throw illegal state exception
            }
            double orderTotal = orderDto.getOrderItemDtoList()
                    .stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(orderItemDto -> orderItemDto.getUnitPrice() * orderItemDto.getQuantity())
                    .sum();


            Orders orders = new Orders(userId, OrderStatus.PENDING, orderTotal);

            orderRepository.save(orders);

            List<OrderItems> orderItemsList = orderDto.getOrderItemDtoList()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(orderItemDto -> {
                        orderItemDto.setOrderItemId(orders.getId());
                        return new OrderItems(orders.getId(), orderItemDto.getProductId(), orderItemDto.getQuantity(), orderItemDto.getUnitPrice());
                    })
                    .collect(Collectors.toList());

            orderItemRepository.saveAll(orderItemsList);

            return new OrderResponseDto(orders.getId(), userId, OrderStatus.PENDING);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
        return null;
    }

    @Override
    public OrderResponseDto getOrder(Long orderId, Long userId) {
        Optional<Orders> orders = orderRepository.findByIdAndCustomerId(orderId, userId);
        if (orders.isPresent()) {
            List<OrderItemDto> orderItemDtoList = getOrderItemDtoFromOrderId(orderId);
            return new OrderResponseDto(orders.get().getId(), orders.get().getCustomerId(), orders.get().getOrderStatus(), orderItemDtoList);
        }

        return null;
    }

    @Override
    public List<OrderResponseDto> listOrders(Long userId, OrderStatus status) {

        List<Orders> ordersList;

        if (status != null) {
            ordersList = orderRepository.findByCustomerIdAndOrderStatus(userId, status);
        } else {
            ordersList = orderRepository.findByCustomerId(userId);
        }

        List<OrderResponseDto> orderResponseDtoList = ordersList.stream().filter(Objects::nonNull).map(order -> {
            List<OrderItemDto> orderItemDtoList = getOrderItemDtoFromOrderId(order.getId());
            return new OrderResponseDto(order.getId(), order.getCustomerId(), order.getOrderStatus(), orderItemDtoList);
        }).collect(Collectors.toList());

        return orderResponseDtoList;
    }

    @Transactional
    @Override
    public OrderDTO cancelOrder(Long orderId, Long userId) {
        Optional<Orders> orders = orderRepository.findByIdAndCustomerId(orderId, userId);
        if (orders.isPresent() && orders.get().getOrderStatus() == OrderStatus.PENDING) {
            orders.get().setOrderStatus(OrderStatus.CANCELED);
            orderRepository.save(orders.get());
            return new OrderDTO(orderId, userId, orders.get().getOrderStatus(), ORDER_CANCELLATION_SUCCESS_MESSAGE);
        }
        return null;
    }

    private List<OrderItemDto> getOrderItemDtoFromOrderId(Long orderId) {
        List<OrderItems> orderItemsList = orderItemRepository.findByOrderId(orderId);
        List<OrderItemDto> orderItemDtoList = orderItemsList.stream()
                .filter(Objects::nonNull)
                .map(orderItems -> new OrderItemDto(orderItems.getId(), orderItems.getProductId(), orderItems.getQuantity(), orderItems.getUnitPrice()))
                .collect(Collectors.toList());
        return orderItemDtoList;
    }
}
