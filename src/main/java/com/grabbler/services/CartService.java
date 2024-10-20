package com.grabbler.services;

import java.util.List;

import com.grabbler.payloads.CartDTO;

public interface CartService {
    CartDTO addProductToCart(Long cartId, Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String email, Long cartId);

    CartDTO updateProductQuantityInCart(Long cartId, Long productId, Integer quantity);

    void updateProductInCart(Long cartId, Long productId);

    String deleteProductFromCart(Long cartId, Long productId);
}
