package com.grabbler.payloads.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchProductRequest {
  private String productName;
  private String description;
  private Double price;
  private Integer quantity;
  private Double discount;
}
