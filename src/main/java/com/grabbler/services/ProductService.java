package com.grabbler.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.grabbler.models.Product;
import com.grabbler.payloads.ProductDTO;

public interface ProductService {
    
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO); 
    
    //TODO: Get all Products 

    //TODO: Search by category
    //
    
    ProductDTO updateProduct(Long productId, Product product);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    //TODO: searchProductByKeyword

    String deleteProduct(Long productId);

}
