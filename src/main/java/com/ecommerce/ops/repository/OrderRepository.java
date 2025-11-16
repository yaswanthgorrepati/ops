package com.ecommerce.ops.repository;

import com.ecommerce.ops.entity.Orders;
import com.ecommerce.ops.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    Optional<Orders> findByIdAndCustomerId(Long id, Long customerId);

    List<Orders> findByCustomerId(Long customerId);

    List<Orders> findByCustomerIdAndOrderStatus(Long customerId, OrderStatus status);


}
