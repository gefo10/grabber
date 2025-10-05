package com.grabbler.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grabbler.models.Order;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user.email = ?1 AND o.orderId = ?2")
    Order findOrderByEmailAndOrderId(String email, Long orderId);

    @Query("SELECT o FROM Order o WHERE o.user.email = ?1")
    List<Order> findAllByUserEmail(String email);

}
