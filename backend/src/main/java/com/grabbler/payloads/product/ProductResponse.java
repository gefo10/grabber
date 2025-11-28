package com.grabbler.payloads.product;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
  private List<ProductDTO> content;
  private Integer pageSize;
  private Integer pageNumber;
  private Long totalElements;
  private Integer totalPages;
  private Boolean lastPage;
}
