package com.grabbler.services;

import com.grabbler.enums.OrderStatus;
import com.grabbler.payloads.order.OrderDTO;
import com.grabbler.payloads.order.OrderItemDTO;
import com.grabbler.payloads.order.OrderResponse;
import com.grabbler.payloads.payment.PaymentDTO;
import java.util.List;

public interface OrderService {

  OrderDTO placeOrder(Long userId, PaymentDTO paymentDTO);

  String cancelOrder(Long orderId, Long userId);

  List<OrderItemDTO> getOrderItems(Long orderId);

  boolean isOrderOwner(Long orderId, Long userId);

  OrderDTO getOrder(String emailId, Long orderId);

  List<OrderDTO> getOrdersByUser(String emailId);

  OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

  OrderDTO updateOrder(String emailId, Long orderId, OrderStatus orderStatus);

  OrderDTO updateOrderStatus(Long orderId, OrderStatus orderStatus);
}
