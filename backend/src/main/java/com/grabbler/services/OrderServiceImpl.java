package com.grabbler.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.grabbler.enums.OrderStatus;
import com.grabbler.exceptions.APIException;
import com.grabbler.exceptions.ResourceNotFoundException;
import com.grabbler.models.Cart;
import com.grabbler.models.CartItem;
import com.grabbler.models.Order;
import com.grabbler.models.OrderItem;
import com.grabbler.models.Payment;
import com.grabbler.models.Product;
import com.grabbler.models.User;
import com.grabbler.payloads.order.OrderDTO;
import com.grabbler.payloads.order.OrderItemDTO;
import com.grabbler.payloads.order.OrderResponse;
import com.grabbler.payloads.payment.PaymentDTO;
import com.grabbler.repositories.OrderItemRepository;
import com.grabbler.repositories.OrderRepository;

import jakarta.transaction.Transactional;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(Long userId, PaymentDTO paymentDTO) {

        Optional<User> userOpt = userService.findUserById(userId);

        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User", "userId", userId);
        }

        User user = userOpt.get();
        Optional<Cart> cart_Optional = cartService.findCartByEmail(user.getEmail());

        if (cart_Optional.isEmpty()) {
            throw new ResourceNotFoundException("Cart", "email", user.getEmail());
        }

        Cart cart = cart_Optional.get();

        Order order = new Order();

        order.setUser(user);
        order.setOrderDate(LocalDate.now());

        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus(OrderStatus.valueOf("PENDING"));

        Payment payment = paymentService.processPayment(paymentDTO);

        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);
        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems.size() == 0) {
            throw new APIException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);

            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();

            // clear cart after order placement
            // cartService.deleteCartItem(user.getEmail(),
            // item.getProduct().getProductId());

            productService.decreaseProductQuantity(product.getProductId(), quantity);
        });

        cart.clear();

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderDTO.setEmail(user.getEmail());

        orderDTO.getOrderItems().stream().map(item -> modelMapper.map(item, OrderItemDTO.class));

        return orderDTO;

    }

    @Override
    public OrderDTO getOrder(String emailId, Long orderId) {
        Order order = orderRepository.findOrderByEmailAndOrderId(emailId, orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }

        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        orderDTO.setEmail(order.getUser().getEmail());

        return orderDTO;
    }

    @Override
    public List<OrderDTO> getOrdersByUser(String emailId) {
        List<Order> orders = orderRepository.findAllByUserEmail(emailId);
        List<OrderDTO> orderDTOs = orders.stream().map(order -> {
            OrderDTO dto = modelMapper.map(order, OrderDTO.class);
            dto.setEmail(order.getUser().getEmail());
            return dto;
        })
                .toList();
        if (orderDTOs.size() == 0) {
            throw new APIException("No orders found for user with emailId: " + emailId);
        }

        return orderDTOs;
    }

    @Override
    public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sort);
        Page<Order> pageOrders = orderRepository.findAll(pageDetails);

        List<Order> orders = pageOrders.getContent();

        List<OrderDTO> orderDTOs = orders.stream().map(order -> {
            OrderDTO dto = modelMapper.map(order, OrderDTO.class);
            dto.setEmail(order.getUser().getEmail());
            return dto;
        }).toList();

        if (orderDTOs.size() == 0) {
            throw new APIException("No orders found");
        }

        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setContent(orderDTOs);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());
        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setTotalElements(pageOrders.getTotalElements());
        orderResponse.setLastPage(pageOrders.isLast());

        return orderResponse;
    }

    @Override
    public OrderDTO updateOrder(String emailId, Long orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findOrderByEmailAndOrderId(emailId, orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }

        order.setOrderStatus(orderStatus);

        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

        return orderDTO;
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setOrderStatus(orderStatus);
            orderRepository.save(order);
            OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
            return orderDTO;
        } else {
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }
    }

    @Override
    public String cancelOrder(Long orderId, Long userId) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);

        if (orderOpt.isEmpty()) {
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }

        Order order = orderOpt.get();

        if (!order.getUser().getUserId().equals(userId)) {
            throw new APIException("You don't have permission to cancel this order");
        }

        if (order.getOrderStatus() == OrderStatus.SHIPPED ||
                order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new APIException("Cannot cancel order that has been shipped or delivered");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productService.save(product);

            // Todo: update product repository
        }

        return "Order cancelled successfully";
    }

    @Override
    public List<OrderItemDTO> getOrderItems(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);

        if (orderOpt.isEmpty()) {
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }

        Order order = orderOpt.get();

        return order.getOrderItems().stream()
                .map(item -> modelMapper.map(item, OrderItemDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    public boolean isOrderOwner(Long orderId, Long userId) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);

        if (orderOpt.isEmpty()) {
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }

        Order order = orderOpt.get();

        return order.getUser().getUserId().equals(userId);
    }

}
