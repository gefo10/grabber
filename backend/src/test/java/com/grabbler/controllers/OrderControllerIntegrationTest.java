package com.grabbler.controllers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grabbler.enums.OrderStatus;
import com.grabbler.enums.PaymentMethod;
import com.grabbler.models.*;
import com.grabbler.payloads.order.CreateOrderRequest;
import com.grabbler.payloads.order.UpdateOrderStatusRequest;
import com.grabbler.payloads.payment.PaymentDTO;
import com.grabbler.repositories.*;
import com.grabbler.security.JwtUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test for Order functionality Tests the complete order workflow from cart to order
 * placement
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Order Integration Tests")
class OrderControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private RoleRepository roleRepository;

  @Autowired private ProductRepository productRepository;

  @Autowired private PaymentRepository paymentRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private CartRepository cartRepository;

  @Autowired private CartItemRepository cartItemRepository;

  @Autowired private OrderItemRepository orderItemRepository;

  @Autowired private OrderRepository orderRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private JwtUtil jwtUtil;

  private String customerToken;
  private String adminToken;
  private User customer;
  private User admin;
  private Product product;
  private Cart cart;

  @BeforeEach
  void setUp() {
    // Clean up all repositories
    orderRepository.deleteAll();
    cartItemRepository.deleteAll();
    cartRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
    userRepository.deleteAll();
    roleRepository.deleteAll();

    // Create roles
    Role customerRole = roleRepository.save(new Role(null, "ROLE_CUSTOMER"));
    Role adminRole = roleRepository.save(new Role(null, "ROLE_ADMIN"));

    // Create customer
    customer = new User();
    customer.setFirstName("John");
    customer.setLastName("Doe");
    customer.setEmail("customer@test.com");
    customer.setPassword(passwordEncoder.encode("password"));
    customer.getRoles().add(customerRole);
    customer = userRepository.save(customer);

    // Create admin
    admin = new User();
    admin.setFirstName("Admin");
    admin.setLastName("User");
    admin.setEmail("admin@test.com");
    admin.setPassword(passwordEncoder.encode("admin"));
    admin.getRoles().add(adminRole);
    admin = userRepository.save(admin);

    // Generate tokens
    customerToken =
        jwtUtil.generateToken(
            customer.getUsername(),
            customer.getEmail(),
            List.of("ROLE_CUSTOMER"),
            customer.getUserId().toString());

    adminToken =
        jwtUtil.generateToken(
            admin.getUsername(),
            admin.getEmail(),
            List.of("ROLE_ADMIN"),
            admin.getUserId().toString());

    // Create category and product
    Category category = new Category();
    category.setCategoryName("Electronics");
    category = categoryRepository.save(category);

    product = new Product();
    product.setProductName("Laptop");
    product.setDescription("High-performance laptop");
    product.setPrice(1000.0);
    product.setQuantity(10);
    product.setDiscount(10.0);
    product.setSpecialPrice(900.0);
    product.setCategory(category);
    product = productRepository.save(product);

    // Create cart for customer
    cart = new Cart();
    cart.setUser(customer);
    cart.setTotalPrice(0.0);
    cart = cartRepository.save(cart);

    customer.setCart(cart);
    userRepository.save(customer);
  }

  @Nested
  @DisplayName("Place Order Tests")
  class PlaceOrderTests {

    @Test
    @DisplayName("Should successfully place order with items in cart")
    void placeOrder_WithItemsInCart_Success() throws Exception {
      // Arrange - Add item to cart
      CartItem cartItem = new CartItem();
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(2);
      cartItem.setProductPrice(product.getSpecialPrice());
      cartItem.setDiscount(product.getDiscount());
      cartItemRepository.save(cartItem);

      cart.getCartItems().add(cartItem);
      cart.setTotalPrice(1800.0); // 900 * 2
      cartRepository.save(cart);

      PaymentDTO paymentDTO = new PaymentDTO();
      paymentDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
      paymentDTO.setPaymentToken("test_token_123");

      CreateOrderRequest request = new CreateOrderRequest();
      request.setPayment(paymentDTO);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/orders")
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.orderId", notNullValue()))
          .andExpect(jsonPath("$.email", is(customer.getEmail())))
          .andExpect(jsonPath("$.totalAmount", is(1800.0)))
          .andExpect(jsonPath("$.orderStatus", is("PENDING")))
          .andExpect(jsonPath("$.orderItems", hasSize(1)))
          .andExpect(jsonPath("$.payment.paymentMethod", is("CREDIT_CARD")));
    }

    @Test
    @DisplayName("Should fail when cart is empty")
    void placeOrder_EmptyCart_ReturnsError() throws Exception {
      // Arrange
      PaymentDTO paymentDTO = new PaymentDTO();
      paymentDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
      paymentDTO.setPaymentToken("test_token_123");

      CreateOrderRequest request = new CreateOrderRequest();
      request.setPayment(paymentDTO);

      // Act & Assert
      mockMvc
          .perform(
              post("/api/v1/orders")
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isUnprocessableEntity())
          .andExpect(jsonPath("$.message", containsString("Cannot place order with empty cart")));
    }

    @Test
    @DisplayName("Should decrease product quantity after order placement")
    void placeOrder_DecreasesProductQuantity() throws Exception {
      // Arrange
      int initialQuantity = product.getQuantity();

      CartItem cartItem = new CartItem();
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(3);
      cartItem.setProductPrice(product.getSpecialPrice());
      cartItem.setDiscount(product.getDiscount());
      cartItemRepository.save(cartItem);

      cart.getCartItems().add(cartItem);
      cart.setTotalPrice(2700.0);
      cartRepository.save(cart);

      PaymentDTO paymentDTO = new PaymentDTO();
      paymentDTO.setPaymentMethod(PaymentMethod.PAYPAL);
      paymentDTO.setPaymentToken("paypal_token");

      CreateOrderRequest request = new CreateOrderRequest();
      request.setPayment(paymentDTO);

      // Act
      mockMvc
          .perform(
              post("/api/v1/orders")
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated());

      // Assert - verify product quantity decreased
      Product updatedProduct = productRepository.findById(product.getProductId()).get();
      assert updatedProduct.getQuantity() == initialQuantity - 3;
    }

    @Test
    @DisplayName("Should clear cart after successful order")
    void placeOrder_ClearsCart_AfterSuccess() throws Exception {
      // Arrange
      CartItem cartItem = new CartItem();
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(1);
      cartItem.setProductPrice(product.getSpecialPrice());
      cartItem.setDiscount(product.getDiscount());
      cartItemRepository.save(cartItem);

      cart.getCartItems().add(cartItem);
      cart.setTotalPrice(900.0);
      cartRepository.save(cart);

      PaymentDTO paymentDTO = new PaymentDTO();
      paymentDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
      paymentDTO.setPaymentToken("test_token");

      CreateOrderRequest request = new CreateOrderRequest();
      request.setPayment(paymentDTO);

      // Act
      mockMvc
          .perform(
              post("/api/v1/orders")
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated());

      // Assert - verify cart is cleared
      Cart updatedCart = cartRepository.findById(cart.getCartId()).get();
      assert updatedCart.getCartItems().isEmpty();
      assert updatedCart.getTotalPrice() == 0.0;
    }
  }

  @Nested
  @DisplayName("Get Orders Tests")
  class GetOrdersTests {

    private Order testOrder;

    @BeforeEach
    void setupOrder() {
      // Create an order for testing
      testOrder = new Order();
      testOrder.setUser(customer);
      testOrder.setOrderDate(java.time.LocalDate.now());
      testOrder.setTotalAmount(900.0);
      testOrder.setOrderStatus(OrderStatus.PENDING);

      Payment payment = new Payment();
      payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
      payment.setPaymentToken("test_token");
      payment.setPaymentStatus(com.grabbler.enums.PaymentStatus.COMPLETED);
      testOrder.setPayment(payment);
      payment.setOrder(testOrder);
      paymentRepository.save(payment);

      testOrder = orderRepository.save(testOrder);
    }

    @Test
    @DisplayName("Customer should see only their own orders")
    void getUserOrders_AsCustomer_ReturnsOwnOrders() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/orders")
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0].email", is(customer.getEmail())));
    }

    @Test
    @DisplayName("Admin should see all orders")
    void getUserOrders_AsAdmin_ReturnsAllOrders() throws Exception {
      // Create order for admin too
      Order adminOrder = new Order();
      adminOrder.setUser(admin);
      adminOrder.setOrderDate(java.time.LocalDate.now());
      adminOrder.setTotalAmount(500.0);
      adminOrder.setOrderStatus(OrderStatus.DELIVERED);
      orderRepository.save(adminOrder);

      mockMvc
          .perform(
              get("/api/v1/orders")
                  .header("Authorization", "Bearer " + adminToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("Should get specific order by ID")
    void getOrderById_ValidId_ReturnsOrder() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/orders/{orderId}", testOrder.getOrderId())
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.orderId", is(testOrder.getOrderId().intValue())))
          .andExpect(jsonPath("$.totalAmount", is(900.0)));
    }

    @Test
    @DisplayName("Should not allow customer to see other customer's orders")
    void getOrderById_OtherCustomerOrder_ReturnsForbidden() throws Exception {
      // Create another customer
      Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER").get();
      User otherCustomer = new User();
      otherCustomer.setFirstName("Jane");
      otherCustomer.setLastName("Smith");
      otherCustomer.setEmail("other@test.com");
      otherCustomer.setPassword(passwordEncoder.encode("password"));
      otherCustomer.getRoles().add(customerRole);
      otherCustomer = userRepository.save(otherCustomer);

      // Create order for other customer
      Order otherOrder = new Order();
      otherOrder.setUser(otherCustomer);
      otherOrder.setOrderDate(java.time.LocalDate.now());
      otherOrder.setTotalAmount(500.0);
      otherOrder.setOrderStatus(OrderStatus.PENDING);

      Payment otherPayment = new Payment();
      otherPayment.setPaymentMethod(PaymentMethod.PAYPAL);
      otherPayment.setPaymentToken("other_token");
      otherPayment.setPaymentStatus(com.grabbler.enums.PaymentStatus.COMPLETED);
      otherPayment = paymentRepository.save(otherPayment); // Save the payment
      otherOrder.setPayment(otherPayment);
      otherOrder = orderRepository.save(otherOrder);

      // Try to access other customer's order
      mockMvc
          .perform(
              get("/api/v1/orders/{orderId}", otherOrder.getOrderId())
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("Cancel Order Tests")
  class CancelOrderTests {

    private Order testOrder;

    @BeforeEach
    void setupOrder() {
      testOrder = new Order();
      testOrder.setUser(customer);
      testOrder.setOrderDate(java.time.LocalDate.now());
      testOrder.setTotalAmount(900.0);
      testOrder.setOrderStatus(OrderStatus.PENDING);

      Payment payment = new Payment();
      payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
      payment.setPaymentToken("test_token");
      payment.setPaymentStatus(com.grabbler.enums.PaymentStatus.COMPLETED);

      payment = paymentRepository.save(payment); // <-- SAVE PAYMENT FIRST
      testOrder.setPayment(payment);
      testOrder = orderRepository.save(testOrder);
    }

    @Test
    @DisplayName("Customer should be able to cancel their pending order")
    void cancelOrder_PendingOrder_Success() throws Exception {
      mockMvc
          .perform(
              delete("/api/v1/orders/{orderId}", testOrder.getOrderId())
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.message", containsString("cancelled successfully")));

      // Verify order status changed
      Order updatedOrder = orderRepository.findById(testOrder.getOrderId()).get();
      assert updatedOrder.getOrderStatus() == OrderStatus.CANCELLED;
    }

    @Test
    @DisplayName("Should not cancel shipped order")
    void cancelOrder_ShippedOrder_ReturnsBadRequest() throws Exception {
      testOrder.setOrderStatus(OrderStatus.SHIPPED);
      orderRepository.save(testOrder);

      mockMvc
          .perform(
              delete("/api/v1/orders/{orderId}", testOrder.getOrderId())
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isUnprocessableEntity())
          .andExpect(jsonPath("$.message", containsString("Cannot cancel")));
    }

    @Test
    @DisplayName("Should not cancel delivered order")
    void cancelOrder_DeliveredOrder_ReturnsBadRequest() throws Exception {
      testOrder.setOrderStatus(OrderStatus.DELIVERED);
      orderRepository.save(testOrder);

      mockMvc
          .perform(
              delete("/api/v1/orders/{orderId}", testOrder.getOrderId())
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isUnprocessableEntity());
    }
  }

  @Nested
  @DisplayName("Update Order Status Tests (Admin Only)")
  class UpdateOrderStatusTests {

    private Order testOrder;

    @BeforeEach
    void setupOrder() {
      testOrder = new Order();
      testOrder.setUser(customer);
      testOrder.setOrderDate(java.time.LocalDate.now());
      testOrder.setTotalAmount(900.0);
      testOrder.setOrderStatus(OrderStatus.PENDING);
      testOrder = orderRepository.save(testOrder);
    }

    @Test
    @DisplayName("Admin should be able to update order status")
    void updateOrderStatus_AsAdmin_Success() throws Exception {
      UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
      request.setStatus(OrderStatus.SHIPPED);

      mockMvc
          .perform(
              patch("/api/v1/orders/{orderId}/status", testOrder.getOrderId())
                  .header("Authorization", "Bearer " + adminToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.orderStatus", is("SHIPPED")));

      // PROCESSING -> SHIPPED
      UpdateOrderStatusRequest request2 = new UpdateOrderStatusRequest();
      request2.setStatus(OrderStatus.SHIPPED);

      mockMvc
          .perform(
              patch("/api/v1/orders/{orderId}/status", testOrder.getOrderId())
                  .header("Authorization", "Bearer " + adminToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request2)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.orderStatus", is("SHIPPED")));

      // SHIPPED -> DELIVERED
      UpdateOrderStatusRequest request3 = new UpdateOrderStatusRequest();
      request3.setStatus(OrderStatus.DELIVERED);

      mockMvc
          .perform(
              patch("/api/v1/orders/{orderId}/status", testOrder.getOrderId())
                  .header("Authorization", "Bearer " + adminToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request3)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.orderStatus", is("DELIVERED")));
    }
  }

  @Nested
  @DisplayName("Order Items Tests")
  class OrderItemsTests {

    private Order testOrder;

    @BeforeEach
    void setupOrderWithItems() {
      testOrder = new Order();
      testOrder.setUser(customer);
      testOrder.setOrderDate(java.time.LocalDate.now());
      testOrder.setTotalAmount(900.0);
      testOrder.setOrderStatus(OrderStatus.PENDING);
      testOrder = orderRepository.save(testOrder);

      // Add order items
      OrderItem orderItem = new OrderItem();
      orderItem.setOrder(testOrder);
      orderItem.setProduct(product);
      orderItem.setQuantity(2);
      orderItem.setOrderedProductPrice(product.getSpecialPrice());
      orderItem.setDiscount(product.getDiscount());

      orderItemRepository.save(orderItem);
    }

    @Test
    @DisplayName("Should get order items for valid order")
    void getOrderItems_ValidOrder_ReturnsItems() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/orders/{orderId}/items", testOrder.getOrderId())
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", isA(List.class)));
    }

    @Test
    @DisplayName("Should not allow customer to see other customer's order items")
    void getOrderItems_OtherCustomerOrder_ReturnsForbidden() throws Exception {
      // Create another customer and their order
      Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER").get();
      User otherCustomer = new User();
      otherCustomer.setFirstName("Jane");
      otherCustomer.setLastName("Smith");
      otherCustomer.setEmail("other@test.com");
      otherCustomer.setPassword(passwordEncoder.encode("password"));
      otherCustomer.getRoles().add(customerRole);
      otherCustomer = userRepository.save(otherCustomer);

      Order otherOrder = new Order();
      otherOrder.setUser(otherCustomer);
      otherOrder.setOrderDate(java.time.LocalDate.now());
      otherOrder.setTotalAmount(500.0);
      otherOrder.setOrderStatus(OrderStatus.PENDING);
      otherOrder = orderRepository.save(otherOrder);

      mockMvc
          .perform(
              get("/api/v1/orders/{orderId}/items", otherOrder.getOrderId())
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("Authorization Tests")
  class AuthorizationTests {

    @Test
    @DisplayName("Should reject requests without authentication")
    void placeOrder_NoAuth_ReturnsUnauthorized() throws Exception {
      PaymentDTO paymentDTO = new PaymentDTO();
      paymentDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
      paymentDTO.setPaymentToken("test_token");

      CreateOrderRequest request = new CreateOrderRequest();
      request.setPayment(paymentDTO);

      mockMvc
          .perform(
              post("/api/v1/orders")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should reject requests with invalid token")
    void getOrders_InvalidToken_ReturnsUnauthorized() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/orders")
                  .header("Authorization", "Bearer invalid_token_xyz")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isUnauthorized());
    }
  }

  @Nested
  @DisplayName("Edge Cases and Error Handling")
  class EdgeCaseTests {

    @Test
    @DisplayName("Should handle invalid payment method gracefully")
    void placeOrder_InvalidPaymentMethod_ReturnsBadRequest() throws Exception {
      CartItem cartItem = new CartItem();
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(1);
      cartItem.setProductPrice(product.getSpecialPrice());
      cartItem.setDiscount(product.getDiscount());
      cartItemRepository.save(cartItem);

      cart.getCartItems().add(cartItem);
      cart.setTotalPrice(900.0);
      cartRepository.save(cart);

      // Try to send invalid JSON
      String invalidJson =
          """
                    {
                        "payment": {
                            "paymentMethod": "INVALID_METHOD",
                            "paymentToken": "test_token"
                        }
                    }
                    """;

      mockMvc
          .perform(
              post("/api/v1/orders")
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(invalidJson))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle non-existent order ID")
    void getOrder_NonExistentId_ReturnsNotFound() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/orders/{orderId}", 99999L)
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should validate payment token is not null")
    void placeOrder_NullPaymentToken_ReturnsBadRequest() throws Exception {
      CartItem cartItem = new CartItem();
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(1);
      cartItem.setProductPrice(product.getSpecialPrice());
      cartItem.setDiscount(product.getDiscount());
      cartItemRepository.save(cartItem);

      cart.getCartItems().add(cartItem);
      cart.setTotalPrice(900.0);
      cartRepository.save(cart);

      PaymentDTO paymentDTO = new PaymentDTO();
      paymentDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
      paymentDTO.setPaymentToken(null); // Null token

      CreateOrderRequest request = new CreateOrderRequest();
      request.setPayment(paymentDTO);

      mockMvc
          .perform(
              post("/api/v1/orders")
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle large order quantity")
    void placeOrder_LargeQuantity_Success() throws Exception {
      // Create product with large quantity
      Product bulkProduct = new Product();
      bulkProduct.setProductName("Bulk Item");
      bulkProduct.setDescription("Bulk item for testing");
      bulkProduct.setPrice(10.0);
      bulkProduct.setQuantity(10000);
      bulkProduct.setDiscount(0.0);
      bulkProduct.setSpecialPrice(10.0);
      bulkProduct.setCategory(product.getCategory());
      bulkProduct = productRepository.save(bulkProduct);

      CartItem cartItem = new CartItem();
      cartItem.setCart(cart);
      cartItem.setProduct(bulkProduct);
      cartItem.setQuantity(1000);
      cartItem.setProductPrice(bulkProduct.getSpecialPrice());
      cartItem.setDiscount(0.0);
      cartItemRepository.save(cartItem);

      cart.getCartItems().add(cartItem);
      cart.setTotalPrice(10000.0);
      cartRepository.save(cart);

      PaymentDTO paymentDTO = new PaymentDTO();
      paymentDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
      paymentDTO.setPaymentToken("test_token");

      CreateOrderRequest request = new CreateOrderRequest();
      request.setPayment(paymentDTO);

      mockMvc
          .perform(
              post("/api/v1/orders")
                  .header("Authorization", "Bearer " + customerToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.totalAmount", is(10000.0)));
    }
  }
}
