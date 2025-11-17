package com.ecommerce.ops.repository;

import com.ecommerce.ops.entity.Orders;
import com.ecommerce.ops.enums.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    Optional<Orders> findByIdAndCustomerId(Long id, Long customerId);

    List<Orders> findByCustomerId(Long customerId);

    List<Orders> findByCustomerIdAndOrderStatus(Long customerId, OrderStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Orders o WHERE o.orderStatus = :status AND o.id > :lastId ORDER BY o.id ASC")
    List<Orders> findPendingOrdersAfterId(@Param("status") OrderStatus status, @Param("lastId") Long lastId,
                                          Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Orders o WHERE o.orderStatus = :status AND o.id >= :startId AND o.id <= :lastId ORDER BY o.id ASC")
    List<Orders> findPendingOrdersBetweenStartAndEndId(@Param("status") OrderStatus status, @Param("startId") Long startId,
                                                       @Param("lastId") Long lastId, Pageable pageable);

}
