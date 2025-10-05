package com.grabbler.services;

import java.util.List;

import com.grabbler.payloads.OrderDTO;
import com.grabbler.payloads.OrderResponse;
import com.grabbler.payloads.PaymentDTO;
import com.grabbler.enums.OrderStatus;

public interface OrderService {

    OrderDTO placeOrder(Long userId, Long cartId, PaymentDTO paymentDTO);

    OrderDTO getOrder(String emailId, Long orderId);

    List<OrderDTO> getOrdersByUser(String emailId);

    OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    OrderDTO updateOrder(String emailId, Long orderId, String orderStatus);

    boolean updateOrderStatus(Long orderId, OrderStatus orderStatus);
}
