package com.grabbler.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.grabbler.enums.*;
import com.grabbler.exceptions.APIException;
import com.grabbler.exceptions.ResourceNotFoundException;
import com.grabbler.models.*;
import com.grabbler.payloads.order.OrderDTO;
import com.grabbler.payloads.payment.*;
import com.grabbler.repositories.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

  @Mock private OrderRepository orderRepository;

  @Mock private OrderItemRepository orderItemRepository;

  @Mock private CartService cartService;

  @Mock private PaymentService paymentService;

  @Mock private ProductService productService;

  @Mock private UserService userService;

  @Mock private ModelMapper modelMapper;

  @InjectMocks private OrderServiceImpl orderService;

  private User user;
  private Cart cart;
  private Product product;
  private PaymentDTO paymentDTO;
  private Payment payment;
  private Order order;
  private CartItem cartItem;
  private OrderDTO orderDTO;

  @BeforeEach
  public void setUp() {
    // Initialize Product FIRST with all required fields
    product = new Product();
    product.setProductId(1L);
    product.setProductName("Test Product");
    product.setQuantity(10); // CRITICAL!
    product.setPrice(100.0);
    product.setDiscount(10.0);
    product.setSpecialPrice(90.0);

    // Initialize CartItem and SET THE PRODUCT
    cartItem = new CartItem();
    cartItem.setCartItemId(1L);
    cartItem.setProduct(product); // CRITICAL! This links product to cartItem
    cartItem.setQuantity(2);
    cartItem.setProductPrice(90.0);
    cartItem.setDiscount(10.0);

    // Initialize User
    user = new User();
    user.setUserId(1L);
    user.setEmail("test@example.com");
    user.setFirstName("Test");
    user.setLastName("User");

    // Initialize Cart with the cartItem
    cart = new Cart();
    cart.setCartId(1L);
    cart.setUser(user);
    cart.setCartItems(new ArrayList<>(Arrays.asList(cartItem))); // Add cartItem to list
    cart.setTotalPrice(180.0);

    // Initialize Payment
    payment = new Payment();
    payment.setTransactionId(1L);
    payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    payment.setPaymentStatus(PaymentStatus.PENDING);
    payment.setPaymentToken("token123");

    // Initialize Order
    order = new Order();
    order.setOrderId(1L);
    order.setUser(user);
    order.setTotalAmount(180.0);
    order.setOrderStatus(OrderStatus.PENDING);
    order.setPayment(payment);
    order.setOrderDate(LocalDate.now());

    // Initialize PaymentDTO
    paymentDTO = new PaymentDTO();
    paymentDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    paymentDTO.setPaymentToken("token123");

    // Initialize OrderDTO
    orderDTO = new OrderDTO();
    orderDTO.setOrderId(1L);
    orderDTO.setEmail(user.getEmail());
    orderDTO.setTotalAmount(180.0);
    orderDTO.setOrderStatus(OrderStatus.PENDING.toString());
  }

  @Test
  public void testPlaceOrder_Success() {
    // Arrange
    when(userService.findUserById(user.getUserId())).thenReturn(Optional.of(user));
    when(cartService.findCartByEmail(user.getEmail())).thenReturn(Optional.of(cart));
    when(productService.getProductById(product.getProductId())).thenReturn(product);
    when(paymentService.processPayment(any(PaymentDTO.class))).thenReturn(payment);
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(orderItemRepository.saveAll(any(List.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(productService.decreaseProductQuantity(
            eq(product.getProductId()), eq(cartItem.getQuantity())))
        .thenReturn(true);
    when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

    // Act
    OrderDTO result = orderService.placeOrder(user.getUserId(), paymentDTO);

    // Assert
    assertNotNull(result);
    assertEquals(orderDTO.getOrderId(), result.getOrderId());

    // Verify the flow
    verify(userService).findUserById(user.getUserId());
    verify(cartService).findCartByEmail(user.getEmail());
    verify(productService).getProductById(product.getProductId());
    verify(paymentService).processPayment(any(PaymentDTO.class));
    verify(orderRepository).save(any(Order.class));
    verify(orderItemRepository).saveAll(any(List.class));
    verify(productService).decreaseProductQuantity(product.getProductId(), cartItem.getQuantity());
  }

  @Test
  public void testPlaceOrder_UserNotFound_ThrowsException() {
    // Arrange
    when(userService.findUserById(user.getUserId())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          orderService.placeOrder(user.getUserId(), paymentDTO);
        });

    verify(userService).findUserById(user.getUserId());
    verify(cartService, never()).findCartByEmail(any());
  }

  @Test
  public void testPlaceOrder_CartNotFound_ThrowsException() {
    // Arrange
    when(userService.findUserById(user.getUserId())).thenReturn(Optional.of(user));
    when(cartService.findCartByEmail(user.getEmail())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          orderService.placeOrder(user.getUserId(), paymentDTO);
        });

    verify(userService).findUserById(user.getUserId());
    verify(cartService).findCartByEmail(user.getEmail());
    verify(orderRepository, never()).save(any());
  }

  @Test
  public void testPlaceOrder_EmptyCart_ThrowsAPIException() {
    // Arrange
    Cart emptyCart = new Cart();
    emptyCart.setCartId(1L);
    emptyCart.setUser(user);
    emptyCart.setCartItems(new ArrayList<>()); // Empty list
    emptyCart.setTotalPrice(0.0);

    when(userService.findUserById(user.getUserId())).thenReturn(Optional.of(user));
    when(cartService.findCartByEmail(user.getEmail())).thenReturn(Optional.of(emptyCart));

    // Act & Assert
    assertThrows(
        APIException.class,
        () -> {
          orderService.placeOrder(user.getUserId(), paymentDTO);
        });

    // Verify that these methods should NOT be called when cart is empty
    verify(paymentService, never()).processPayment(any());
    verify(orderRepository, never()).save(any()); // CHANGE: Use never() instead of expecting it
    verify(orderItemRepository, never()).saveAll(any());
    verify(productService, never()).decreaseProductQuantity(anyLong(), anyInt());
  }

  @Test
  public void testPlaceOrder_MultipleItems() {
    // Arrange - create second product
    Product product2 = new Product();
    product2.setProductId(2L);
    product2.setProductName("Product 2");
    product2.setQuantity(20);
    product2.setPrice(50.0);
    product2.setDiscount(5.0);
    product2.setSpecialPrice(47.5);

    // Create second cart item
    CartItem cartItem2 = new CartItem();
    cartItem2.setCartItemId(2L);
    cartItem2.setProduct(product2);
    cartItem2.setQuantity(3);
    cartItem2.setProductPrice(47.5);
    cartItem2.setDiscount(5.0);
    cartItem2.setCart(cart); // Set the cart reference

    // Create new cart items list with BOTH items
    List<CartItem> cartItems = new ArrayList<>();
    cartItems.add(cartItem); // From @BeforeEach
    cartItems.add(cartItem2); // New one

    // Create a NEW cart for this test (don't modify the shared one)
    Cart testCart = new Cart();
    testCart.setCartId(1L);
    testCart.setUser(user);
    testCart.setCartItems(cartItems);
    testCart.setTotalPrice(322.5);

    // Update order and orderDTO
    order.setTotalAmount(322.5);
    orderDTO.setTotalAmount(322.5);

    // Mock setup - use testCart instead of cart
    when(userService.findUserById(user.getUserId())).thenReturn(Optional.of(user));
    when(cartService.findCartByEmail(user.getEmail())).thenReturn(Optional.of(testCart));

    // Mock getProductById for BOTH products
    when(productService.getProductById(product.getProductId())).thenReturn(product);
    when(productService.getProductById(product2.getProductId())).thenReturn(product2);

    when(paymentService.processPayment(any(PaymentDTO.class))).thenReturn(payment);
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(orderItemRepository.saveAll(any(List.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Mock product quantity decrease for both products
    when(productService.decreaseProductQuantity(product.getProductId(), cartItem.getQuantity()))
        .thenReturn(true);
    when(productService.decreaseProductQuantity(product2.getProductId(), cartItem2.getQuantity()))
        .thenReturn(true);

    // Mock ModelMapper
    when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);

    // Act
    OrderDTO result = orderService.placeOrder(user.getUserId(), paymentDTO);

    // Assert
    assertNotNull(result);
    assertEquals(orderDTO.getOrderId(), result.getOrderId());

    // Verify the flow
    verify(userService).findUserById(user.getUserId());
    verify(cartService).findCartByEmail(user.getEmail());
    verify(productService).getProductById(product.getProductId());
    verify(productService).getProductById(product2.getProductId());
    verify(paymentService).processPayment(any(PaymentDTO.class));
    verify(orderRepository).save(any(Order.class));
    verify(orderItemRepository).saveAll(any(List.class));
    verify(productService, times(2)).decreaseProductQuantity(anyLong(), anyInt());
  }
}
