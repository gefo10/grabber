package com.grabbler.services;

import java.util.List;
import java.util.Optional;

import com.grabbler.models.Cart;
import com.grabbler.payloads.cart.CartDTO;

public interface CartService {
    CartDTO addProductToCart(Long cartId, Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCartByEmail(String email);

    CartDTO updateCartItem(String userEmail, Long itemId, Integer quantity);

    void updateProductInCart(Long cartId, Long productId);

    String deleteProductFromCart(Long cartId, Long productId);

    Optional<Cart> findCartByEmail(String email);

    Optional<Cart> findByCartId(Long cartId);
}
