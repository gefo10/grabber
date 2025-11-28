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
import com.grabbler.payloads.order.OrderItemDTO;
import com.grabbler.payloads.payment.*;
import com.grabbler.repositories.*;
import java.time.LocalDate;
import java.util.ArrayList;
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
    // Set up user
    user = new User();
    user.setUserId(1L);
    user.setEmail("test@test.com");

    // Set up product
    product = new Product();
    product.setProductId(1L);
    product.setProductName("Test Product");
    product.setPrice(10.0);
    product.setQuantity(10);
    product.setSpecialPrice(10.0);
    product.setDiscount(0.0);

    // Set up cart item
    cartItem = new CartItem();
    cartItem.setCartItemId(1L);
    cartItem.setProduct(product);
    cartItem.setQuantity(2);
    cartItem.setProductPrice(10.0);
    cartItem.setDiscount(0.0);

    // Set up cart - IMPORTANT: create mutable list
    cart = new Cart();
    cart.setCartId(1L);
    cart.setUser(user);
    cart.setTotalPrice(20.0);
    cart.setCartItems(new ArrayList<>(List.of(cartItem)));

    // Set up payment DTO
    paymentDTO = new PaymentDTO();
    paymentDTO.setPaymentToken("test_token");
    paymentDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    paymentDTO.setPaymentStatus(PaymentStatus.PENDING);

    // Set up payment
    payment = new Payment();
    payment.setTransactionId(1L);
    payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    payment.setPaymentToken("test_token");
    payment.setPaymentStatus(PaymentStatus.COMPLETED);

    // Set up order
    order = new Order();
    order.setOrderId(1L);
    order.setUser(user);
    order.setOrderDate(LocalDate.now());
    order.setTotalAmount(20.0);
    order.setOrderStatus(OrderStatus.PENDING);
    order.setPayment(payment);
    order.setOrderItems(new ArrayList<>());

    // Set up order DTO
    orderDTO = new OrderDTO();
    orderDTO.setOrderId(1L);
    orderDTO.setEmail(user.getEmail());
    orderDTO.setOrderItems(new ArrayList<>());
    orderDTO.setTotalAmount(20.0);
  }

  @Test
  public void testPlaceOrder_Success() {
    // Arrange
    when(userService.findUserById(user.getUserId())).thenReturn(Optional.of(user));
    when(cartService.findCartByEmail(user.getEmail())).thenReturn(Optional.of(cart));
    when(paymentService.processPayment(any(PaymentDTO.class))).thenReturn(payment);
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    // Mock saving order items
    when(orderItemRepository.saveAll(any(List.class)))
        .thenAnswer(
            invocation -> {
              List<OrderItem> items = invocation.getArgument(0);
              return items; // Return the same list
            });

    // Mock the cart item deletion - this is called for each item in the cart
    when(cartService.deleteCartItem(eq(user.getEmail()), eq(product.getProductId())))
        .thenReturn("Item deleted");

    // Mock product quantity decrease
    when(productService.decreaseProductQuantity(
            eq(product.getProductId()), eq(cartItem.getQuantity())))
        .thenReturn(true);

    // Mock ModelMapper
    when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);
    when(modelMapper.map(any(OrderItem.class), eq(OrderItemDTO.class)))
        .thenReturn(new OrderItemDTO());

    // Act
    OrderDTO result = orderService.placeOrder(user.getUserId(), paymentDTO);

    // Assert
    assertNotNull(result);

    // Verify the flow
    verify(userService).findUserById(user.getUserId());
    verify(cartService).findCartByEmail(user.getEmail());
    verify(paymentService).processPayment(paymentDTO);
    verify(orderRepository).save(any(Order.class));
    verify(orderItemRepository).saveAll(any(List.class));

    // Verify cart item was deleted
    verify(cartService).deleteCartItem(user.getEmail(), product.getProductId());

    // Verify product quantity was decreased
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
    when(paymentService.processPayment(any(PaymentDTO.class))).thenReturn(payment);
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    // Act & Assert
    assertThrows(
        APIException.class,
        () -> {
          orderService.placeOrder(user.getUserId(), paymentDTO);
        });

    // Verify order was saved but items were not
    verify(orderRepository).save(any(Order.class));
    verify(orderItemRepository, never()).saveAll(any());
  }

  @Test
  public void testPlaceOrder_MultipleItems() {
    // Arrange - cart with 2 items
    Product product2 = new Product();
    product2.setProductId(2L);
    product2.setProductName("Product 2");
    product2.setPrice(20.0);
    product2.setQuantity(5);
    product2.setSpecialPrice(20.0);

    CartItem cartItem2 = new CartItem();
    cartItem2.setCartItemId(2L);
    cartItem2.setProduct(product2);
    cartItem2.setQuantity(1);
    cartItem2.setProductPrice(20.0);

    cart.getCartItems().add(cartItem2);
    cart.setTotalPrice(40.0);

    when(userService.findUserById(user.getUserId())).thenReturn(Optional.of(user));
    when(cartService.findCartByEmail(user.getEmail())).thenReturn(Optional.of(cart));
    when(paymentService.processPayment(any(PaymentDTO.class))).thenReturn(payment);
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(orderItemRepository.saveAll(any(List.class))).thenAnswer(inv -> inv.getArgument(0));

    // Mock deletions for both items
    when(cartService.deleteCartItem(eq(user.getEmail()), eq(product.getProductId())))
        .thenReturn("Item deleted");
    when(cartService.deleteCartItem(eq(user.getEmail()), eq(product2.getProductId())))
        .thenReturn("Item deleted");

    when(productService.decreaseProductQuantity(anyLong(), anyInt())).thenReturn(true);
    when(modelMapper.map(any(Order.class), eq(OrderDTO.class))).thenReturn(orderDTO);
    when(modelMapper.map(any(OrderItem.class), eq(OrderItemDTO.class)))
        .thenReturn(new OrderItemDTO());

    // Act
    OrderDTO result = orderService.placeOrder(user.getUserId(), paymentDTO);

    // Assert
    assertNotNull(result);
    verify(cartService, times(2)).deleteCartItem(eq(user.getEmail()), anyLong());
    verify(productService, times(2)).decreaseProductQuantity(anyLong(), anyInt());
  }
}
