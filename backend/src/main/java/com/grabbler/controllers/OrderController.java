package com.grabbler.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import com.grabbler.payloads.ApiResponse;
import com.grabbler.payloads.order.*;
import com.grabbler.payloads.payment.*;
import com.grabbler.services.OrderService;
import com.grabbler.enums.OrderStatus;
import com.grabbler.models.User;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    public OrderService orderService;

    @Operation(summary = "Create order", description = "Place an order from the user's cart")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        OrderDTO orderDTO = orderService.placeOrder(user.getUserId(), request.getPayment());

        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }

    @Operation(summary = "Get user's orders", description = "List all orders for the authenticated user")
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getUserOrders(
            Authentication authentication,
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "orderDate", required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "ASC", required = false) String sortOrder) {

        User user = (User) authentication.getPrincipal();

        // Check if user is admin
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // Return all orders with pagination
            OrderResponse response = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
            return ResponseEntity.ok(response.getContent());
        } else {
            // Return only user's orders
            List<OrderDTO> orders = orderService.getOrdersByUser(user.getEmail());
            return ResponseEntity.ok(orders);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#orderId, authentication.principal.userId)")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId, Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        OrderDTO orderDTO = orderService.getOrder(user.getEmail(), orderId);
        return ResponseEntity.ok(orderDTO);
    }

    @Operation(summary = "Cancel order", description = "Cancel an order (if status allows)")
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#orderId, authentication.principal.userId)")
    public ResponseEntity<ApiResponse<?>> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        String message = orderService.cancelOrder(orderId, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @Operation(summary = "Get order items", description = "List all items in a specific order")
    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#orderId, authentication.principal.userId)")
    public ResponseEntity<List<OrderItemDTO>> getOrderItems(@PathVariable Long orderId) {
        List<OrderItemDTO> items = orderService.getOrderItems(orderId);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Update order status", description = "Update the status of an order (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        OrderDTO orderDTO = orderService.updateOrderStatus(
                orderId,
                request.getStatus());

        return ResponseEntity.ok(orderDTO);
    }

}
