package com.grabbler.services;

import com.grabbler.exceptions.APIException;
import com.grabbler.exceptions.ResourceNotFoundException;
import com.grabbler.models.Cart;
import com.grabbler.models.CartItem;
import com.grabbler.models.Product;
import com.grabbler.payloads.cart.CartDTO;
import com.grabbler.payloads.cart.CartItemDTO;
import com.grabbler.payloads.product.ProductDTO;
import com.grabbler.repositories.CartItemRepository;
import com.grabbler.repositories.CartRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    @Override
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public CartDTO addProductToUserCart(String email, Long productId, Integer quantity) {
        Cart cart = cartRepository
                .findCartByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "email", email));

        Product product = productService.getProductById(productId);

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cart.getCartId());

        if (cartItem != null) {
            throw new APIException("Product" + productId + " already exists in the cart");
        }

        if (product.getQuantity() == 0) {
            throw new APIException("Product" + productId + " is out of stock");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException(
                    "Product" + productId + " has only " + product.getQuantity() + " items in stock");
        }

        product.setQuantity(product.getQuantity() - quantity);
        productService.save(product);

        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setProductPrice(product.getSpecialPrice());
        newCartItem.setDiscount(product.getDiscount());

        cartItemRepository.save(newCartItem);
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItemDTO> itemDTOs = cart.getCartItems().stream()
                .map(item -> {
                    ProductDTO pDto = modelMapper.map(item.getProduct(), ProductDTO.class);
                    return new CartItemDTO(item.getCartItemId(), pDto, item.getQuantity(), item.getDiscount(), item.getProductPrice(), item.getProductPrice() * item.getQuantity());
                })
                .collect(Collectors.toList());

        cartDTO.setCartItems(itemDTOs);

        return cartDTO;
    }

    @Recover
    public CartDTO recoverAddProductToUserCart(
            Exception e, String email, Long productId, Integer quantity) {
        log.error("Failed to add product {} to cart after retries for user: {}", productId, email, e);
        throw new APIException(
                "Unable to add product to cart. Another user may have purchased the last items. Please refresh and try again.");
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()) {
            throw new APIException("No carts found");
        }

        List<CartDTO> cartDTOs = carts.stream()
                .map(
                        c -> {
                            CartDTO cartDTO = modelMapper.map(c, CartDTO.class);
                            List<CartItemDTO> cartItemDTOs= c.getCartItems().stream()
                                    .map(item -> { 
                                        ProductDTO pDto = modelMapper.map(item.getProduct(), ProductDTO.class);
                                        return new CartItemDTO(item.getCartItemId(), pDto, item.getQuantity(), item.getDiscount(), item.getProductPrice(), item.getProductPrice() + item.getQuantity());
                                    })
                                    .collect(Collectors.toList());
                            cartDTO.setCartItems(cartItemDTOs);
                            return cartDTO;
                        })
                .collect(Collectors.toList());

        return cartDTOs;
    }

    @Override
    public CartDTO getCartByEmail(String email) {
        Optional<Cart> cart_Optional = cartRepository.findCartByUserEmail(email);

        if (cart_Optional.isEmpty()) {
            throw new ResourceNotFoundException("Cart", "email", email);
        }

        Cart cart = cart_Optional.get();

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItemDTO> cartItemDTOs = cart.getCartItems().stream()
                 .map(item -> { 
                    ProductDTO pDto = modelMapper.map(item.getProduct(), ProductDTO.class);
                    return new CartItemDTO(item.getCartItemId(), pDto, item.getQuantity(), item.getDiscount(), item.getProductPrice(), item.getProductPrice() + item.getQuantity());
                 })
                .collect(Collectors.toList());

        cartDTO.setCartItems(cartItemDTOs);

        return cartDTO;
    }

    @Override
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public CartDTO updateCartItem(String userEmail, Long itemId, Integer quantity) {

        Cart cart = cartRepository
                .findCartByUserEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user email", userEmail));

        Product product = productService.getProductById(itemId);

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(itemId, cart.getCartId());

        if (cartItem == null) {
            throw new APIException("Product" + product.getProductName() + " does not exist in the cart");
        }

        int quantityDifference = quantity - cartItem.getQuantity();

        if (quantityDifference > 0 && product.getQuantity() < quantityDifference) {
            throw new APIException(
                    "Cannot update cart. Only " + product.getQuantity() + " items available in stock.");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        product.setQuantity(product.getQuantity() + cartItem.getQuantity() - quantity);

        cartItem.setQuantity(quantity);
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setDiscount(product.getDiscount());

        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItemDTO> cartItemDTOs = cart.getCartItems().stream()
                 .map(item -> { 
                    ProductDTO pDto = modelMapper.map(item.getProduct(), ProductDTO.class);
                    return new CartItemDTO(item.getCartItemId(), pDto, item.getQuantity(), item.getDiscount(), item.getProductPrice(), item.getProductPrice() + item.getQuantity());
                 })
                .collect(Collectors.toList());

        cartDTO.setCartItems(cartItemDTOs);

        return cartDTO;
    }

    @Recover
    public CartDTO recoverUpdateCartItem(
            Exception e, String userEmail, Long itemId, Integer quantity) {
        log.error("Failed to update cart item {} after retries for user: {}", itemId, userEmail, e);
        throw new APIException(
                "Unable to update cart item. Inventory may have changed. Please refresh and try again.");
    }

    @Override
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional
    public CartDTO deleteCartItem(String email, Long cartItemId) {
        Cart cart = cartRepository
                .findCartByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "email", email));

        CartItem cartItem = cartItemRepository.findByCartItemId(cartItemId);

        if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
            throw new APIException("Access Denied: Item does not belong to your cart!");
        }

        deleteCartItemFromCart(cart, cartItem);

        return modelMapper.map(cart, CartDTO.class);
    }

    @Recover
    public String recoverDeleteCartItem(Exception e, String email, Long cartItemId) {
        log.error("Failed to delete cart item {} after retries for user: {}", cartItemId, email, e);
        throw new APIException("Unable to remove item from cart. Please refresh and try again.");
    }

    @Override
    public Optional<Cart> findCartByEmail(String email) {
        return cartRepository.findCartByUserEmail(email);
    }

    @Override
    public Optional<Cart> findByCartId(Long cartId) {
        return cartRepository.findById(cartId);
    }

    @Override
    @Transactional
    public String clearCart(String email) {
        Optional<Cart> cartOpt = cartRepository.findCartByUserEmail(email);

        if (cartOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cart", "email", email);
        }

        Cart cart = cartOpt.get();

        List<CartItem> items = new ArrayList<>(cart.getCartItems());

        for (CartItem item : items) {
            try {
                deleteCartItemFromCart(cart, item);
            } catch (Exception e) {
                throw new APIException("Cart item could not be deleted");
            }
        }

        return "Cart cleared successfully";
    }

    private void deleteCartItemFromCart(Cart cart, CartItem cartItem) {

        //orphan removal true -> hibarnate deletes the row for us
        cart.getCartItems().remove(cartItem);

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        Product product = cartItem.getProduct();
        product.setQuantity(product.getQuantity() + cartItem.getQuantity());
        productService.save(product);

        cart.setTotalPrice(cartPrice);
        cart.getCartItems().remove(cartItem);
        cartRepository.save(cart);

    }

    @Override
    public CartDTO cartToDto(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        return cartDTO;
    }
}
