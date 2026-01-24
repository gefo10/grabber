package com.grabbler.repositories;

import com.grabbler.models.CartItem;
import com.grabbler.models.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartItemId(Long cartItemId);

    @Query("SELECT ci.product FROM CartItem ci WHERE ci.product.productId = ?1")
    Product findByProductId(Long productId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.product.productId = ?1 And ci.cart.cartId = ?2")
    CartItem findCartItemByProductIdAndCartId(Long productId, Long cartId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.product.productId = ?1 And ci.cart.cartId = ?2")
    void deleteCartItemByProductIdAndCartId(Long productId, Long cartId);

    @Modifying
    @Transactional
    void deleteCartItemByCartItemId(Long cartItemId);
}
