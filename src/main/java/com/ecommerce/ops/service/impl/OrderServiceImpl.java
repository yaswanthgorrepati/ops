package com.ecommerce.ops.service.impl;

import com.ecommerce.ops.dto.OrderDTO;
import com.ecommerce.ops.dto.OrderItemDto;
import com.ecommerce.ops.dto.OrderResponseDto;
import com.ecommerce.ops.entity.AuditLogs;
import com.ecommerce.ops.entity.OrderItems;
import com.ecommerce.ops.entity.Orders;
import com.ecommerce.ops.enums.OrderStatus;
import com.ecommerce.ops.exception.ApiException;
import com.ecommerce.ops.exception.ResponseCode;
import com.ecommerce.ops.repository.AuditLogRepository;
import com.ecommerce.ops.repository.OrderItemRepository;
import com.ecommerce.ops.repository.OrderRepository;
import com.ecommerce.ops.service.OrderService;
import com.ecommerce.ops.utils.AuditUtil;
import com.ecommerce.ops.utils.Constants;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Transactional
    @Override
    public OrderResponseDto createOrder(List<OrderItemDto> orderItemDtoList, Long userId) {
        try {
            logger.info("validating the create order request");
            ResponseCode responseCode = validateCreateOrderRequest(orderItemDtoList, userId);

            if (responseCode != ResponseCode.SUCCESS)
                throw new ApiException(responseCode);

            double orderTotal = orderItemDtoList
                    .stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(orderItemDto -> orderItemDto.getUnitPrice() * orderItemDto.getQuantity())
                    .sum();
            logger.info("Total order sum is :{}", orderTotal);
            if (isNullOrNonPositiveDouble(orderTotal)) {
                throw new ApiException(ResponseCode.INVALID_ORDER_ITEM_LIST);
            }

            Orders orders = new Orders(userId, OrderStatus.PENDING, orderTotal);

            orderRepository.save(orders);
            logger.info("Order save orderId: {}", orders.getId());

            List<OrderItems> orderItemsList = orderItemDtoList
                    .stream()
                    .filter(Objects::nonNull)
                    .map(orderItemDto -> {
                        orderItemDto.setOrderItemId(orders.getId());
                        return new OrderItems(orders.getId(), orderItemDto.getProductId(), orderItemDto.getQuantity(), orderItemDto.getUnitPrice());
                    })
                    .collect(Collectors.toList());

            orderItemRepository.saveAll(orderItemsList);
            logger.info("Order items saved successfully");

            return new OrderResponseDto(orders.getId(), userId, orders.getOrderStatus());
        } catch (Exception e) {
            logger.error("Error occurred in create order: {}", e.getStackTrace());
            throw new ApiException(ResponseCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    public OrderResponseDto getOrder(Long orderId, Long userId) {
        try {
            logger.info(" Validating the getOrder details");
            if (isNullOrNonPositiveNumber(orderId) || isNullOrNonPositiveNumber(userId)) {
                throw new ApiException(ResponseCode.INVALID_ORDER_ID_USER_ID_);
            }

            Optional<Orders> orders = orderRepository.findByIdAndCustomerId(orderId, userId);

            if (orders.isEmpty()) {
                logger.error("No order details found");
                throw new ApiException(ResponseCode.EMPTY_ORDER_FOUND);
            }

            List<OrderItemDto> orderItemDtoList = getOrderItemDtoFromOrderId(orders.get().getId());

            if (CollectionUtils.isEmpty(orderItemDtoList)) {
                logger.error("No order item details found");
                throw new ApiException(ResponseCode.EMPTY_ORDER_ITEMS);
            }

            return new OrderResponseDto(orders.get().getId(), orders.get().getCustomerId(), orders.get().getOrderStatus(), orderItemDtoList);
        } catch (Exception e) {
            logger.error("Error occurred in getOrder: {}", e.getStackTrace());
            throw new ApiException(ResponseCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    public List<OrderResponseDto> listOrders(Long userId, OrderStatus status) {
        try {
            logger.info("Retrieving all the orders for the userId:{}", userId);
            List<Orders> ordersList;
            if (isNullOrNonPositiveNumber(userId)) {
                logger.error("Invalid userId");
                throw new ApiException(ResponseCode.NO_USER_ID);
            }

            if (status != null) {
                ordersList = orderRepository.findByCustomerIdAndOrderStatus(userId, status);
            } else {
                ordersList = orderRepository.findByCustomerId(userId);
            }

            if (CollectionUtils.isEmpty(ordersList)) {
                logger.error("Order details are not found");
                throw new ApiException(ResponseCode.ORDER_NOT_FOUND);
            }

            List<OrderResponseDto> orderResponseDtoList = ordersList.stream()
                    .filter(Objects::nonNull)
                    .map(order -> {
                        List<OrderItemDto> orderItemDtoList = getOrderItemDtoFromOrderId(order.getId());
                        if (CollectionUtils.isEmpty(orderItemDtoList)) {
                            logger.error("No order items are found for orderId:{}", order.getId());
                            throw new ApiException(ResponseCode.EMPTY_ORDER_ITEMS);
                        }
                        return new OrderResponseDto(order.getId(), order.getCustomerId(), order.getOrderStatus(), orderItemDtoList);
                    }).collect(Collectors.toList());

            return orderResponseDtoList;
        } catch (Exception e) {
            logger.error("Error occurred in listOrders: {}", e.getStackTrace());
            throw new ApiException(ResponseCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    @Transactional
    @Override
    public OrderDTO cancelOrder(Long orderId, Long userId) {
        try {
            logger.info("Cancelling the order:{}", orderId);
            if (isNullOrNonPositiveNumber(orderId) || isNullOrNonPositiveNumber(userId)) {
                throw new ApiException(ResponseCode.INVALID_ORDER_ID_USER_ID_);
            }

            Optional<Orders> orders = orderRepository.findByIdAndCustomerId(orderId, userId);

            if (orders.isEmpty()) {
                logger.error("No order details are found");
                throw new ApiException(ResponseCode.EMPTY_ORDER_FOUND);
            }

            if (orders.get().getOrderStatus() != OrderStatus.PENDING) {
                logger.info("Order is not pending status to cancel, so skipping the cancel");
                return new OrderDTO(orderId, userId, orders.get().getOrderStatus(), Constants.ORDER_NOT_CANCELLED);
            }

            logger.info("Order cancelled");
            String prevData = AuditUtil.toJson(orders.get());

            orders.get().setOrderStatus(OrderStatus.CANCELED);
            orderRepository.save(orders.get());

            logger.info("creating audit logs");
            String currData = AuditUtil.toJson(orders.get());
            AuditLogs auditLogs = new AuditLogs(Orders.class.getSimpleName(), prevData, currData);
            auditLogRepository.save(auditLogs);

            return new OrderDTO(orderId, userId, orders.get().getOrderStatus(), Constants.ORDER_CANCELLATION_SUCCESS_MESSAGE);
        } catch (Exception e) {
            logger.error("Error occurred in cancelOrder: {}", e.getStackTrace());
            throw new ApiException(ResponseCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    private ResponseCode validateCreateOrderRequest(List<OrderItemDto> orderItemDtoList, Long userId) {
        if (isNullOrNonPositiveNumber(userId)) {
            return ResponseCode.NO_USER_ID;
        }
        if (CollectionUtils.isEmpty(orderItemDtoList)) {
            return ResponseCode.EMPTY_ORDER_ITEMS;
        }

        Optional<OrderItemDto> invalidOrderItemDto = orderItemDtoList.stream().filter(Objects::nonNull)
                .filter(oid -> isNullOrNonPositiveNumber(oid.getProductId())
                        || isNullOrNonPositiveNumber(oid.getQuantity()) || isNullOrNonPositiveDouble(oid.getUnitPrice()))
                .findAny();

        if (invalidOrderItemDto.isPresent()) {
            return ResponseCode.INVALID_ORDER_ITEM_LIST;
        }

        logger.info("Validated the create order request");

        return ResponseCode.SUCCESS;
    }

    private List<OrderItemDto> getOrderItemDtoFromOrderId(Long orderId) {
        List<OrderItems> orderItemsList = orderItemRepository.findByOrderId(orderId);

        if (CollectionUtils.isEmpty(orderItemsList)) {
            throw new ApiException(ResponseCode.EMPTY_ORDER_ITEMS);
        }

        List<OrderItemDto> orderItemDtoList = orderItemsList.stream()
                .filter(Objects::nonNull)
                .map(orderItems -> new OrderItemDto(orderItems.getId(), orderItems.getProductId(), orderItems.getQuantity(), orderItems.getUnitPrice()))
                .collect(Collectors.toList());
        logger.info("Returning the orderItems");
        return orderItemDtoList;
    }

    private boolean isNullOrNonPositiveNumber(Number n) {
        return Objects.isNull(n) || n.longValue() <= 0;
    }

    private boolean isNullOrNonPositiveDouble(Double d) {
        return Objects.isNull(d) || d <= 0.0;
    }
}
