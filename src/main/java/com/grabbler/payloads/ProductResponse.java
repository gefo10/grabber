package com.grabbler.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
