package com.grabbler.repositories;

import com.grabbler.models.Order;
import com.grabbler.models.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Optional<Payment> findByTransactionId(Long transactionId);

  List<Payment> findByOrder(Order order);
}
