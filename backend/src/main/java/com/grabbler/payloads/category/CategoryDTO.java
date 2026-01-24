package com.grabbler.payloads.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long categoryId;
    private String categoryName;
    // private List<ProductDTO> products = new ArrayList<>();
}
