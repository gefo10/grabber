package com.grabbler.payloads.cart;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Long cartId;
    private Double totalPrice = 0.0;
    private List<CartItemDTO> cartItems = new ArrayList<>();
}
