package com.grabbler.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grabbler.exceptions.APIException;
import com.grabbler.exceptions.ResourceNotFoundException;
import com.grabbler.models.Cart;
import com.grabbler.models.CartItem;
import com.grabbler.models.Product;
import com.grabbler.payloads.cart.CartDTO;
import com.grabbler.payloads.product.ProductDTO;
import com.grabbler.repositories.CartItemRepository;
import com.grabbler.repositories.CartRepository;
import jakarta.transaction.Transactional;

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

    @Override
    @Transactional
    public CartDTO addProductToUserCart(String email, Long productId, Integer quantity) {
        Cart cart = cartRepository.findCartByUserEmail(email)
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
            throw new APIException("Product" + productId + " has only " + product.getQuantity() + " items in stock");
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setProductPrice(product.getSpecialPrice());
        newCartItem.setDiscount(product.getDiscount());

        cartItemRepository.save(newCartItem);

        // TODO : should i update productRepo and cartRepo here or not?!
        product.setQuantity(product.getQuantity() - quantity);

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                .collect(Collectors.toList());

        cartDTO.setProducts(productDTOs);

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()) {
            throw new APIException("No carts found");
        }

        List<CartDTO> cartDTOs = carts.stream().map(c -> {
            CartDTO cartDTO = modelMapper.map(c, CartDTO.class);
            List<ProductDTO> productDTOs = c.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                    .collect(Collectors.toList());
            cartDTO.setProducts(productDTOs);
            return cartDTO;
        }).collect(Collectors.toList());

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
        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                .collect(Collectors.toList());

        cartDTO.setProducts(productDTOs);

        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(String userEmail, Long itemId,
            Integer quantity) {

        Cart cart = cartRepository.findCartByUserEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user email", userEmail));

        Product product = productService.getProductById(itemId);

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(itemId, cart.getCartId());

        if (cartItem == null) {
            throw new APIException("Product" + product.getProductName() + " does not exist in the cart");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        product.setQuantity(product.getQuantity() + cartItem.getQuantity() - quantity);

        cartItem.setQuantity(quantity);
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setDiscount(product.getDiscount());

        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                .collect(Collectors.toList());

        cartDTO.setProducts(productDTOs);

        return cartDTO;
    }

    @Override
    @Transactional
    public String deleteCartItem(String email, Long cartItemId) {
        Cart cart = cartRepository.findCartByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "email", email));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartItemId, cart.getCartId());

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", cartItemId);
        }

        deleteCartItemFromCart(cart, cartItem);
        return "Product" + cartItem.getProduct().getProductName() + " deleted from the cart";
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
        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        Product product = cartItem.getProduct();
        product.setQuantity(product.getQuantity() + cartItem.getQuantity());

        // TODO, save product back
        cart.setTotalPrice(cartPrice);
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartItem.getCartItemId(), cart.getCartId());
    }

    @Override
    public CartDTO cartToDto(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        return cartDTO;
    }
}
