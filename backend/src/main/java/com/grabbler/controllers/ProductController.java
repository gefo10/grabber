package com.grabbler.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;

import com.grabbler.models.Product;
import com.grabbler.payloads.product.*;
import com.grabbler.services.ProductService;

import jakarta.validation.Valid;
import com.grabbler.payloads.ApiResponse;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    // ==================== Public Endpoints ====================

    @Operation(summary = "Get all products", description = "List all products with pagination and sorting")
    @GetMapping
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "productId", required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "ASC", required = false) String sortOrder,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        ProductResponse response;

        if (category != null) {
            response = productService.getProductsByCategory(category, pageNumber, pageSize, sortBy, sortOrder);
        } else if (minPrice != null || maxPrice != null) {
            response = productService.getProductsByPriceRange(minPrice, maxPrice, pageNumber, pageSize, sortBy,
                    sortOrder);
        } else {
            response = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product by ID", description = "Get detailed information about a specific product")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long productId) {
        ProductDTO productDTO = productService.getProductDTOById(productId);
        return ResponseEntity.ok(productDTO);
    }

    @Operation(summary = "Search products", description = "Search products by keyword in name or description")
    @GetMapping("/search")
    public ResponseEntity<ProductResponse> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        ProductResponse response = productService.searchProductByKeyword(q, page, size, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    // ==================== Admin Endpoints ====================

    @Operation(summary = "Create product", description = "Create a new product (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDTO productDTO = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    @Operation(summary = "Update product", description = "Update an existing product (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductDTO productDTO = productService.updateProduct(productId, request);
        return ResponseEntity.ok(productDTO);
    }

    @Operation(summary = "Partial update product", description = "Partially update a product (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductDTO> partialUpdateProduct(
            @PathVariable Long productId,
            @RequestBody PatchProductRequest request) {

        ProductDTO productDTO = productService.partialUpdateProduct(productId, request);
        return ResponseEntity.ok(productDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long productId) {
        String status = productService.deleteProduct(productId);

        return ResponseEntity.ok(ApiResponse.success(status));
    }
}
