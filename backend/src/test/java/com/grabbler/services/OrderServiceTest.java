package com.grabbler.services;

import com.grabbler.models.*;
import com.grabbler.payloads.payment.*;
import com.grabbler.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import com.grabbler.enums.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Cart cart;
    private Product product;
    private PaymentDTO paymentDTO;

    @BeforeEach
    public void setUp() {
        // Set up mock objects before each test
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@test.com");

        product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");
        product.setPrice(10.0);
        product.setQuantity(10);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setProductPrice(20.0);

        cart = new Cart();
        cart.setCartId(1L);
        cart.setUser(user);
        cart.setTotalPrice(20.0);
        cart.getCartItems().add(cartItem);

        paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentToken("test_token");
        paymentDTO.setPaymentMethod(PaymentMethod.valueOf("CREDIT_CARD"));
    }

    @Test
    public void testPlaceOrder_Success() {
        // Mock the behavior of the dependencies
        when(cartService.findByCartId(any(Long.class))).thenReturn(Optional.of(cart));
        when(paymentService.processPayment(any(PaymentDTO.class))).thenReturn(new Payment());
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());
        when(userService.findUserById(any(Long.class))).thenReturn(Optional.of(user));
        when(productService.decreaseProductQuantity(any(Long.class), any(Integer.class))).thenReturn(true);
        when(orderItemRepository.saveAll(any(Iterable.class))).thenReturn(new ArrayList<>());

        // Call the method under test
        orderService.placeOrder(user.getUserId(), paymentDTO);

        // Verify that the necessary methods were called exactly once
        verify(cartService, times(1)).findByCartId(cart.getCartId());
        verify(paymentService, times(1)).processPayment(paymentDTO);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productService, times(1)).decreaseProductQuantity(any(Long.class), any(Integer.class));
    }
}
