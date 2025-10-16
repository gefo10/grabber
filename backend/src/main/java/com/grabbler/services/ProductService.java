package com.grabbler.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.grabbler.models.Product;
import com.grabbler.payloads.product.*;

public interface ProductService {

    ProductDTO addProduct(Long categoryId, Product productDTO);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder);

    ProductDTO updateProduct(Long productId, Product product);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder);

    String deleteProduct(Long productId);

    Product getProductById(Long productId);

    boolean decreaseProductQuantity(Long productId, Integer quantity);

}
