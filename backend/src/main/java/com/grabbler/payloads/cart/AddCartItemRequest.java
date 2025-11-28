package com.grabbler.payloads.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCartItemRequest {
  @NotNull private Long productId;

  @NotNull
  @Min(value = 1, message = "Quantity must be at least 1")
  private Integer quantity;
}
