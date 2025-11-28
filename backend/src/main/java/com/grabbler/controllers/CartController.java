package com.grabbler.controllers;

import com.grabbler.exceptions.APIException;
import com.grabbler.models.Cart;
import com.grabbler.models.User;
import com.grabbler.payloads.ApiResponse;
import com.grabbler.payloads.cart.AddCartItemRequest;
import com.grabbler.payloads.cart.CartDTO;
import com.grabbler.payloads.cart.UpdateCartItemRequest;
import com.grabbler.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CartController {

  @Autowired public CartService cartService;

  @Operation(
      summary = "Get current user's cart",
      description = "Retrieve the authenticated user's shopping cart")
  @GetMapping("/cart")
  public ResponseEntity<?> getCurrentUserCart(Authentication authentication) {
    User user = (User) authentication.getPrincipal();

    CartDTO cart = cartService.getCartByEmail(user.getEmail());

    return new ResponseEntity<CartDTO>(cart, HttpStatus.OK);
  }

  @Operation(
      summary = "Add item to cart",
      description = "Add a product to the authenticated user's cart")
  @PostMapping("/cart/items")
  public ResponseEntity<CartDTO> addItemToCart(
      @Valid @RequestBody AddCartItemRequest request, Authentication authentication) {
    User user = (User) authentication.getPrincipal();

    CartDTO cartDTO =
        cartService.addProductToUserCart(
            user.getEmail(), request.getProductId(), request.getQuantity());

    return ResponseEntity.status(HttpStatus.CREATED).body(cartDTO);
  }

  @Operation(summary = "Update cart item", description = "Update quantity of an item in cart")
  @PutMapping("/cart/items/{itemId}")
  public ResponseEntity<CartDTO> updateCartItem(
      @PathVariable Long itemId,
      @Valid @RequestBody UpdateCartItemRequest request,
      Authentication authentication) {

    User user = (User) authentication.getPrincipal();

    CartDTO cartDTO = cartService.updateCartItem(user.getEmail(), itemId, request.getQuantity());

    return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
  }

  @Operation(summary = "Clear cart", description = "Remove all items from the cart")
  @DeleteMapping("/cart")
  public ResponseEntity<ApiResponse<?>> clearCart(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    String message = cartService.clearCart(user.getEmail());
    return ResponseEntity.ok(ApiResponse.success(message));
  }

  @Operation(
      summary = "Remove item from cart",
      description = "Remove a specific item from the cart")
  @DeleteMapping("/cart/items/{itemId}")
  public ResponseEntity<ApiResponse<?>> deleteCartItem(
      @PathVariable Long itemId, Authentication authentication) {
    User user = (User) authentication.getPrincipal();

    String status = cartService.deleteCartItem(user.getEmail(), itemId);

    return ResponseEntity.ok(ApiResponse.success(status));
  }

  // ==================== Admin Operations ====================

  @Operation(
      summary = "Get all carts",
      description = "Admin endpoint to retrieve all shopping carts")
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/carts")
  public ResponseEntity<List<CartDTO>> getAllCarts() {
    List<CartDTO> carts = cartService.getAllCarts();
    return ResponseEntity.ok(carts);
  }

  @Operation(summary = "Get cart by ID", description = "Admin endpoint to get a specific cart")
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/carts/{cartId}")
  public ResponseEntity<?> getCartById(@PathVariable Long cartId) {
    Optional<Cart> cartOpt = cartService.findByCartId(cartId);

    if (cartOpt.isEmpty()) {
      throw new APIException(String.format("Cart with id %d was not found", cartId));
    }

    Cart cartDTO = cartOpt.get();

    return ResponseEntity.ok(cartDTO);
  }
}
