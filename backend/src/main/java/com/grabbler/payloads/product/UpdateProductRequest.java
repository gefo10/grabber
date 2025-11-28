package com.grabbler.payloads.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {
  @NotBlank(message = "Product name is required")
  private String productName;

  @NotBlank(message = "Description is required")
  private String description;

  @NotNull(message = "Price is required")
  @Min(value = 0, message = "Price must be positive")
  private Double price;

  @NotNull(message = "Quantity is required")
  @Min(value = 0, message = "Quantity must be positive")
  private Integer quantity;

  @Min(value = 0, message = "Discount must be between 0 and 100")
  @Max(value = 100, message = "Discount must be between 0 and 100")
  private Double discount;
}
