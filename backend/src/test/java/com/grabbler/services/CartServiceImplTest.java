package com.grabbler.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.grabbler.exceptions.APIException;
import com.grabbler.exceptions.ResourceNotFoundException;
import com.grabbler.models.*;
import com.grabbler.payloads.cart.CartDTO;
import com.grabbler.payloads.cart.CartItemDTO;
import com.grabbler.payloads.product.ProductDTO;
import com.grabbler.repositories.CartItemRepository;
import com.grabbler.repositories.CartRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Unit Tests")
class CartServiceImplTest {

  @Mock private CartRepository cartRepository;

  @Mock private ProductService productService;

  @Mock private CartItemRepository cartItemRepository;

  @Mock private ModelMapper modelMapper;

  @InjectMocks private CartServiceImpl cartService;

  private User user;
  private Cart cart;
  private Product product1;
  private Product product2;
  private CartItem cartItem1;
  private CartItemDTO cartItem1DTO;
  private CartDTO cartDTO;
  private ProductDTO productDTO1;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserId(1L);
    user.setEmail("test@example.com");

    product1 = new Product("Laptop", "Fast Laptop", 10, 1500.0, null);
    product1.setProductId(1L);
    product2 = new Product("Mouse", "Wireless Mouse", 20, 50.0, null);
    product2.setProductId(2L);

    cart = new Cart();
    cart.setCartId(1L);
    cart.setUser(user);
    cart.setTotalPrice(1350.0);
    cart.setCartItems(new ArrayList<>());

    cartItem1 =
        new CartItem(1L, cart, product1, 1, product1.getSpecialPrice(), product1.getDiscount());
    cart.getCartItems().add(cartItem1);

    productDTO1 = new ProductDTO(1L, "Laptop", "img.jpg", "Fast Laptop", 10, 1500.0, 10.0, 1350.0);
    cartItem1DTO= new CartItemDTO(1L, cartDTO, productDTO1, 2, 0.0, 199.99);
    cartDTO = new CartDTO(1L, 1350.0, List.of(cartItem1DTO));
  }

  @Test
  void addProductToUserCart_Success() {
    // Arrange
    when(cartRepository.findCartByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
    when(productService.getProductById(product2.getProductId())).thenReturn(product2);
    when(cartItemRepository.findCartItemByProductIdAndCartId(
            product2.getProductId(), cart.getCartId()))
        .thenReturn(null);
    when(cartItemRepository.save(any(CartItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(modelMapper.map(any(Cart.class), eq(CartDTO.class))).thenReturn(cartDTO);
    when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO1);

    // Act
    CartDTO resultCartDTO =
        cartService.addProductToUserCart(user.getEmail(), product2.getProductId(), 1);

    // Assert
    assertNotNull(resultCartDTO);
    verify(cartRepository).findCartByUserEmail(user.getEmail());
    verify(productService).getProductById(product2.getProductId());
    verify(cartItemRepository).save(any(CartItem.class));
  }

  @Test
  void addProductToUserCart_CartNotFound_ThrowsResourceNotFoundException() {
    // Arrange
    when(cartRepository.findCartByUserEmail(user.getEmail())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          cartService.addProductToUserCart(user.getEmail(), product1.getProductId(), 1);
        });
  }

  @Test
  void addProductToUserCart_ProductAlreadyInCart_ThrowsAPIException() {
    // Arrange
    when(cartRepository.findCartByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
    when(productService.getProductById(product1.getProductId())).thenReturn(product1);
    when(cartItemRepository.findCartItemByProductIdAndCartId(
            product1.getProductId(), cart.getCartId()))
        .thenReturn(cartItem1);

    // Act & Assert
    assertThrows(
        APIException.class,
        () -> {
          cartService.addProductToUserCart(user.getEmail(), product1.getProductId(), 1);
        });
  }

  @Test
  void addProductToUserCart_InsufficientStock_ThrowsAPIException() {
    // Arrange
    product2.setQuantity(0);
    when(cartRepository.findCartByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
    when(productService.getProductById(product2.getProductId())).thenReturn(product2);
    when(cartItemRepository.findCartItemByProductIdAndCartId(
            product2.getProductId(), cart.getCartId()))
        .thenReturn(null);

    // Act & Assert
    APIException exception =
        assertThrows(
            APIException.class,
            () -> {
              cartService.addProductToUserCart(user.getEmail(), product2.getProductId(), 1);
            });
    assertTrue(exception.getMessage().contains("is out of stock"));
  }

  @Test
  void updateCartItem_Success() {
    // Arrange
    int newQuantity = 3;
    when(cartRepository.findCartByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
    when(productService.getProductById(product1.getProductId())).thenReturn(product1);
    when(cartItemRepository.findCartItemByProductIdAndCartId(
            product1.getProductId(), cart.getCartId()))
        .thenReturn(cartItem1);
    when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem1);
    when(modelMapper.map(any(Cart.class), eq(CartDTO.class))).thenReturn(cartDTO);
    when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO1);

    // Act
    CartDTO resultCartDTO =
        cartService.updateCartItem(user.getEmail(), product1.getProductId(), newQuantity);

    // Assert
    assertNotNull(resultCartDTO);
    assertEquals(newQuantity, cartItem1.getQuantity());
  }

  @Test
  void deleteCartItem_Success() {
    // Arrange
    when(cartRepository.findCartByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
    when(cartItemRepository.findCartItemByProductIdAndCartId(
            product1.getProductId(), cart.getCartId()))
        .thenReturn(cartItem1);
    when(productService.save(any(Product.class))).thenReturn(product1);
    // Key fix: deleteCartItemByProductIdAndCartId takes cartItemId (not productId)
    // and cartId
    doNothing()
        .when(cartItemRepository)
        .deleteCartItemByProductIdAndCartId(eq(1L), eq(cart.getCartId()));

    // Act
    String result = cartService.deleteCartItem(user.getEmail(), product1.getProductId());

    // Assert
    assertTrue(result.contains("deleted from the cart"));
  }

  @Test
  void clearCart_Success() {
    // Arrange - Create a completely new cart for this test to avoid conflicts
    Cart freshCart = new Cart();
    freshCart.setCartId(2L);
    freshCart.setUser(user);
    freshCart.setCartItems(new ArrayList<>());

    // Create new products with fresh state
    Product prod1 = new Product("Laptop", "Fast Laptop", 5, 1500.0, null);
    prod1.setProductId(10L);
    Product prod2 = new Product("Mouse", "Wireless Mouse", 15, 50.0, null);
    prod2.setProductId(20L);

    CartItem item1 = new CartItem(100L, freshCart, prod1, 1, 1350.0, 10.0);
    CartItem item2 = new CartItem(200L, freshCart, prod2, 1, 50.0, 0.0);

    freshCart.getCartItems().add(item1);
    freshCart.getCartItems().add(item2);
    freshCart.setTotalPrice(1400.0);

    // Mock the cart lookup
    when(cartRepository.findCartByUserEmail(user.getEmail())).thenReturn(Optional.of(freshCart));

    // Mock product save
    when(productService.save(any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Mock deletion - note: first parameter is cartItemId, second is cartId
    doNothing().when(cartItemRepository).deleteCartItemByProductIdAndCartId(eq(10L), eq(2L));
    doNothing().when(cartItemRepository).deleteCartItemByProductIdAndCartId(eq(20L), eq(2L));

    // Act
    String result = cartService.clearCart(user.getEmail());

    // Assert
    assertEquals("Cart cleared successfully", result);
    verify(cartRepository).findCartByUserEmail(user.getEmail());
    // Verify deletions happened (2 items deleted)
    verify(cartItemRepository).deleteCartItemByProductIdAndCartId(eq(10L), eq(2L));
    verify(cartItemRepository).deleteCartItemByProductIdAndCartId(eq(20L), eq(2L));
    verify(productService, times(2)).save(any(Product.class));
  }

  @Test
  void clearCart_CartNotFound_ThrowsResourceNotFoundException() {
    // Arrange
    when(cartRepository.findCartByUserEmail(user.getEmail())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          cartService.clearCart(user.getEmail());
        });
  }
}
