package com.grabbler.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.grabbler.models.Product;
import com.grabbler.payloads.product.CreateProductRequest;
import com.grabbler.payloads.product.PatchProductRequest;
import com.grabbler.payloads.product.ProductDTO;
import com.grabbler.payloads.product.ProductResponse;
import com.grabbler.payloads.product.UpdateProductRequest;

public interface ProductService {

    ProductDTO createProduct(CreateProductRequest request);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder);

    ProductDTO updateProduct(Long productId, UpdateProductRequest request);

    ProductDTO partialUpdateProduct(Long productId, PatchProductRequest request);

    Product getProductById(Long productId);

    ProductDTO getProductDTOById(Long productId);

    String deleteProductImage(Long productId);

    ProductResponse getProductsByPriceRange(
            Double minPrice,
            Double maxPrice,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder);

    String deleteProduct(Long productId);

    boolean decreaseProductQuantity(Long productId, Integer quantity);

    Product save(Product product);

}
