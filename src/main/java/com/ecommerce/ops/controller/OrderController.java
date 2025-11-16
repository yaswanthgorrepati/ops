package com.ecommerce.ops.controller;

import ch.qos.logback.core.util.StringUtil;
import com.ecommerce.ops.dto.OrderCancelDTO;
import com.ecommerce.ops.dto.OrderRequestDto;
import com.ecommerce.ops.dto.OrderResponseDto;
import com.ecommerce.ops.enums.OrderStatus;
import com.ecommerce.ops.service.OrderService;
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

    @PostMapping("/order")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto,
                                                        @RequestHeader("user-id") Long userId) {
        OrderResponseDto orderResponseDto = orderService.createOrder(orderRequestDto, userId);
        return new ResponseEntity<>(orderResponseDto, HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderByOrderId(@PathVariable("orderId") Long orderId,
                                                              @RequestHeader("user-id") Long userId) {
        OrderResponseDto orderResponseDto = orderService.getOrder(orderId, userId);
        return new ResponseEntity<>(orderResponseDto, HttpStatus.OK);
    }

    @GetMapping("/order")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(@RequestParam(required = false) String status,
                                                               @RequestHeader("user-id") Long userId) {
        OrderStatus orderStatus = null;
        if (StringUtil.notNullNorEmpty(status)) {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        }
        List<OrderResponseDto> orderResponseDtoList = orderService.listOrders(userId, orderStatus);
        return new ResponseEntity<>(orderResponseDtoList, HttpStatus.OK);
    }

    @PutMapping("/order/{orderId}")
    public ResponseEntity<OrderCancelDTO> cancelPendingOrder(@PathVariable("orderId") Long orderId,
                                                             @RequestHeader("user-id") Long userId) {
        OrderCancelDTO orderCancelDTO = orderService.cancelOrder(orderId, userId);
        return new ResponseEntity<>(orderCancelDTO, HttpStatus.OK);
    }
}
