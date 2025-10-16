package com.grabbler.repositories;

import com.grabbler.models.Order;
import com.grabbler.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import com.grabbler.enums.OrderStatus;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = "com.grabbler.models")
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("customer@example.com");
        user.setPassword("password");
        entityManager.persist(user);

        order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(150.75);
        order.setOrderStatus(OrderStatus.valueOf("PENDING"));
        entityManager.persist(order);
        entityManager.flush();
    }

    @Test
    public void whenFindOrderByEmailAndOrderId_thenReturnOrder() {
        // when
        Order found = orderRepository.findOrderByEmailAndOrderId(user.getEmail(), order.getOrderId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getTotalAmount()).isEqualTo(150.75);
    }

    @Test
    public void whenFindAllByUserEmail_thenReturnOrderList() {
        // when
        List<Order> orders = orderRepository.findAllByUserEmail(user.getEmail());

        // then
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getUser().getEmail()).isEqualTo(user.getEmail());
    }
}
