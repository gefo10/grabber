package com.grabbler.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grabbler.payloads.order.*;
import com.grabbler.payloads.payment.*;
import com.grabbler.services.OrderService;
import com.grabbler.enums.OrderStatus;

//TODO: refactor the endpoints to match the REST conventions
@RestController
@RequestMapping("/api/v1")
public class OrderController {
    @Autowired
    public OrderService orderService;

    @PreAuthorize("")
    @PostMapping("/public/users/{userId}/carts/{cartId}/placeOrder")
    public ResponseEntity<OrderDTO> orderProducts(
            @PathVariable Long userId,
            @PathVariable Long cartId,
            @RequestBody PaymentDTO paymnetDTO) {
        OrderDTO orderDTO = orderService.placeOrder(userId, cartId, paymnetDTO);

        return new ResponseEntity<OrderDTO>(orderDTO, HttpStatus.CREATED);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrders(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "ASC", required = false) String sortOrder) {
        OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or #emailId == authentication.principal.username")
    @GetMapping("/public/users/{emailId}/orders")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable String emailId) {
        List<OrderDTO> orderDTOs = orderService.getOrdersByUser(emailId);

        return new ResponseEntity<List<OrderDTO>>(orderDTOs, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or #emailId == authentication.principal.username")
    @GetMapping("/public/users/{emailId}/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderByUser(@PathVariable String emailId, @PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrder(emailId, orderId);
        return new ResponseEntity<OrderDTO>(orderDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/users/{emailId}/orders/{orderId}/orderStatus/{orderStatus}")
    public ResponseEntity<OrderDTO> updateOrderByUser(@PathVariable String emailId, @PathVariable Long orderId,
            @PathVariable String orderStatus) {
        OrderDTO orderDTO = orderService.updateOrder(emailId, orderId, OrderStatus.valueOf(orderStatus));
        return new ResponseEntity<OrderDTO>(orderDTO, HttpStatus.OK);
    }

}
