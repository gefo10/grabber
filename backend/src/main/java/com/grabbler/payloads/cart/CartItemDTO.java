package com.grabbler.payloads.cart;

import com.grabbler.payloads.product.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private Long cartItemId;
    private ProductDTO product;
    private Integer quantity;
    private double discount;
    private double productPrice;
    private double subTotal; // productPrice * quantity
}
