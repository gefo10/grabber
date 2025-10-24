package com.grabbler.services;

import java.util.List;
import java.util.Optional;

import com.grabbler.models.Cart;
import com.grabbler.payloads.cart.CartDTO;

public interface CartService {
    CartDTO addProductToUserCart(String email, Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCartByEmail(String email);

    CartDTO updateCartItem(String userEmail, Long itemId, Integer quantity);

    String deleteCartItem(String email, Long cartItemId);

    String clearCart(String email);

    Optional<Cart> findCartByEmail(String email);

    Optional<Cart> findByCartId(Long cartId);

    CartDTO cartToDto(Cart cart);
}
