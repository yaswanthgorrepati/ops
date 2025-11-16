package com.ecommerce.ops.controller;

import ch.qos.logback.core.util.StringUtil;
import com.ecommerce.ops.dto.OrderDTO;
import com.ecommerce.ops.dto.OrderItemDto;
import com.ecommerce.ops.dto.OrderResponseDto;
import com.ecommerce.ops.enums.OrderStatus;
import com.ecommerce.ops.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecommerce")
public class OrderController {

    @Autowired
    private OrderService orderService;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PostMapping("/order")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody List<OrderItemDto> orderItemDtoList,
                                                        @RequestHeader("user-id") Long userId) {
        logger.info("Creating order for user: {}", userId);

        OrderResponseDto orderResponseDto = orderService.createOrder(orderItemDtoList, userId);

        logger.info("Order created successfully for user: {}", userId);
        return new ResponseEntity<>(orderResponseDto, HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderByOrderId(@PathVariable("orderId") Long orderId,
                                                              @RequestHeader("user-id") Long userId) {
        logger.info("Retrieving the order details for orderId:{} and userId:{}", orderId, userId);
        OrderResponseDto orderResponseDto = orderService.getOrder(orderId, userId);
        logger.info("Order details retrieved");
        return new ResponseEntity<>(orderResponseDto, HttpStatus.OK);
    }

    @GetMapping("/order")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(@RequestParam(required = false) String status,
                                                               @RequestHeader("user-id") Long userId) {
        logger.info("Get all Order for usersId:{} and status:{}", userId, status);
        OrderStatus orderStatus = null;
        if (StringUtil.notNullNorEmpty(status)) {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        }
        List<OrderResponseDto> orderResponseDtoList = orderService.listOrders(userId, orderStatus);
        logger.info("Returning all the orders of a user");
        return new ResponseEntity<>(orderResponseDtoList, HttpStatus.OK);
    }

    @PutMapping("/order/{orderId}")
    public ResponseEntity<OrderDTO> cancelPendingOrder(@PathVariable("orderId") Long orderId,
                                                       @RequestHeader("user-id") Long userId) {
        logger.info("Cancel the order for orderId: {} and userId:{}", orderId, userId);
        OrderDTO orderCancelDTO = orderService.cancelOrder(orderId, userId);
        logger.info("Cancel order message: {}", orderCancelDTO.getMessage());
        return new ResponseEntity<>(orderCancelDTO, HttpStatus.OK);
    }
}
