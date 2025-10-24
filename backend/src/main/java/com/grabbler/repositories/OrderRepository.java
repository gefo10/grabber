package com.grabbler.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grabbler.enums.OrderStatus;
import com.grabbler.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user.email = ?1 AND o.orderId = ?2")
    Order findOrderByEmailAndOrderId(String email, Long orderId);

    @Query("SELECT o FROM Order o WHERE o.user.email = ?1")
    List<Order> findAllByUserEmail(String email);

    Optional<Order> findByOrderId(Long orderId);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o WHERE o.orderId = ?1 AND o.user.userId = ?2")
    boolean existsByOrderIdAndUserId(Long orderId, Long userId);

    @Query("SELECT o FROM Order o WHERE o.orderStatus = ?1")
    List<Order> findByOrderStatus(OrderStatus status);


    @Query("SELECT o FROM Order o  WHERE o.user.userId = ?1")
    List<Order> findAllByUserId(Long userId);

}
