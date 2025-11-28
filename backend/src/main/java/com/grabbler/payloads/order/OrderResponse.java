package com.grabbler.payloads.order;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
  private List<OrderDTO> content;
  private Integer pageSize;
  private Integer pageNumber;
  private Long totalElements;
  private Integer totalPages;
  private Boolean lastPage;
}
