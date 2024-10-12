package com.grabbler.services;

import java.util.List;

import com.grabbler.payloads.OrderDTO;

public interface OrderService {

    OrderDTO placeOrder(String emailId, Long cartId, String paymentMethod);

    OrderDTO getOrder(String emailId, Long orderId);

    List<OrderDTO> getOrdersByUser(String emailId);

    //TODO : Get all Orders
    
    OrderDTO updateOrder(String emailId, Long orderId, String orderStatus);
}
