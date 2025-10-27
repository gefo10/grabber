//package com.grabbler.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.grabbler.models.Category;
//import com.grabbler.models.Product;
//import com.grabbler.models.Role;
//import com.grabbler.models.User;
//import com.grabbler.payloads.product.CreateProductRequest;
//import com.grabbler.payloads.product.UpdateProductRequest;
//import com.grabbler.repositories.CategoryRepository;
//import com.grabbler.repositories.ProductRepository;
//import com.grabbler.repositories.RoleRepository;
//import com.grabbler.repositories.UserRepository;
//import com.grabbler.security.JwtUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.hamcrest.Matchers.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Integration test for ProductController
// * Tests the entire stack: Controller → Service → Repository → Database
// */
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//class ProductControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    private String adminToken;
//    private String userToken;
//    private Category category;
//    private Product product;
//
//    @BeforeEach
//    void setUp() {
//        // Clean up
//        productRepository.deleteAll();
//        categoryRepository.deleteAll();
//        userRepository.deleteAll();
//        roleRepository.deleteAll();
//
//        // Create roles
//        Role adminRole = new Role(null, "ROLE_ADMIN");
//        Role customerRole = new Role(null, "ROLE_CUSTOMER");
//        adminRole = roleRepository.save(adminRole);
//        customerRole = roleRepository.save(customerRole);
//
//        // Create admin user
//        User admin = new User();
//        admin.setFirstName("Admin");
//        admin.setLastName("User");
//        admin.setEmail("admin@test.com");
//        admin.setPassword(passwordEncoder.encode("admin123"));
//        admin.getRoles().add(adminRole);
//        admin = userRepository.save(admin);
//
//        // Create regular user
//        User customer = new User();
//        customer.setFirstName("John");
//        customer.setLastName("Doe");
//        customer.setEmail("user@test.com");
//        customer.setPassword(passwordEncoder.encode("user123"));
//        customer.getRoles().add(customerRole);
//        customer = userRepository.save(customer);
//
//        // Generate tokens
//        adminToken = jwtUtil.generateToken(
//            admin.getUsername(),
//            admin.getEmail(),
//            List.of("ROLE_ADMIN"),
//            admin.getUserId().toString()
//        );
//
//        userToken = jwtUtil.generateToken(
//            customer.getUsername(),
//            customer.getEmail(),
//            List.of("ROLE_CUSTOMER"),
//            customer.getUserId().toString()
//        );
//
//        // Create test category
//        category = new Category();
//        category.setCategoryName("Electronics");
//        category = categoryRepository.save(category);
//
//        // Create test product
//        product = new Product();
//        product.setProductName("Laptop");
//        product.setDescription("High-performance laptop");
//        product.setPrice(1000.0);
//        product.setQuantity(10);
//        product.setDiscount(10.0);
//        product.setSpecialPrice(900.0);
//        product.setCategory(category);
//        product = productRepository.save(product);
//    }
//
//    // ==================== GET Tests ====================
//
//    @Test
//    void getAllProducts_ReturnsProductList() throws Exception {
//        mockMvc.perform(get("/api/v1/products")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.content", hasSize(1)))
//            .andExpect(jsonPath("$.content[0].productName", is("Laptop")))
//            .andExpect(jsonPath("$.content[0].price", is(1000.0)))
//            .andExpect(jsonPath("$.pageNumber", is(0)))
//            .andExpect(jsonPath("$.totalElements", is(1)));
//    }
//
//    @Test
//    void getAllProducts_WithPagination_ReturnsCorrectPage() throws Exception {
//        // Create additional products
//        for (int i = 1; i <= 15; i++) {
//            Product p = new Product();
//            p.setProductName("Product " + i);
//            p.setDescription("Description " + i);
//            p.setPrice(100.0 * i);
//            p.setQuantity(5);
//            p.setDiscount(0.0);
//            p.setSpecialPrice(100.0 * i);
//            p.setCategory(category);
//            productRepository.save(p);
//        }
//
//        mockMvc.perform(get("/api/v1/products")
//                .param("pageNumber", "1")
//                .param("pageSize", "10")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.content", hasSize(6))) // 16 total, page 1 has 6
//            .andExpect(jsonPath("$.pageNumber", is(1)))
//            .andExpect(jsonPath("$.totalElements", is(16)))
//            .andExpect(jsonPath("$.totalPages", is(2)));
//    }
//
//    @Test
//    void getProductById_ExistingProduct_ReturnsProduct() throws Exception {
//        mockMvc.perform(get("/api/v1/products/{productId}", product.getProductId())
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.productName", is("Laptop")))
//            .andExpect(jsonPath("$.description", is("High-performance laptop")))
//            .andExpect(jsonPath("$.price", is(1000.0)));
//    }
//
//    @Test
//    void getProductById_NonExistentProduct_ReturnsNotFound() throws Exception {
//        mockMvc.perform(get("/api/v1/products/{productId}", 9999L)
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void searchProducts_WithKeyword_ReturnsMatchingProducts() throws Exception {
//        mockMvc.perform(get("/api/v1/products/search")
//                .param("q", "Laptop")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.content", hasSize(1)))
//            .andExpect(jsonPath("$.content[0].productName", containsString("Laptop")));
//    }
//
//    @Test
//    void searchProducts_NoMatches_ReturnsEmptyList() throws Exception {
//        mockMvc.perform(get("/api/v1/products/search")
//                .param("q", "NonExistentProduct")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.content", hasSize(0)));
//    }
//
//    @Test
//    void getProductsByCategory_ValidCategory_ReturnsProducts() throws Exception {
//        mockMvc.perform(get("/api/v1/products")
//                .param("category", category.getCategoryId().toString())
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.content", hasSize(1)))
//            .andExpect(jsonPath("$.content[0].productName", is("Laptop")));
//    }
//
//    @Test
//    void getProductsByPriceRange_ValidRange_ReturnsProducts() throws Exception {
//        mockMvc.perform(get("/api/v1/products")
//                .param("minPrice", "500")
//                .param("maxPrice", "1500")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.content", hasSize(1)));
//    }
//
//    // ==================== POST Tests (Admin Only) ====================
//
//    @Test
//    void createProduct_AsAdmin_ReturnsCreatedProduct() throws Exception {
//        CreateProductRequest request = new CreateProductRequest();
//        request.setProductName("New Laptop");
//        request.setDescription("Brand new laptop");
//        request.setPrice(1500.0);
//        request.setQuantity(5);
//        request.setDiscount(5.0);
//        request.setCategoryId(category.getCategoryId());
//
//        mockMvc.perform(post("/api/v1/products")
//                .header("Authorization", "Bearer " + adminToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//            .andExpect(status().isCreated())
//            .andExpect(jsonPath("$.productName", is("New Laptop")))
//            .andExpect(jsonPath("$.price", is(1500.0)))
//            .andExpect(jsonPath("$.specialPrice", is(1425.0))); // 1500 - 5%
//    }
//
//    @Test
//    void createProduct_AsUser_ReturnsForbidden() throws Exception {
//        CreateProductRequest request = new CreateProductRequest();
//        request.setProductName("New Laptop");
//        request.setDescription("Brand new laptop");
//        request.setPrice(1500.0);
//        request.setQuantity(5);
//        request.setDiscount(5.0);
//        request.setCategoryId(category.getCategoryId());
//
//        mockMvc.perform(post("/api/v1/products")
//                .header("Authorization", "Bearer " + userToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//            .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void createProduct_InvalidData_ReturnsBadRequest() throws Exception {
//        CreateProductRequest request = new CreateProductRequest();
//        request.setProductName(""); // Invalid: too short
//        request.setDescription("Short"); // Invalid: too short
//        request.setPrice(-100.0); // Invalid: negative
//        request.setQuantity(-1); // Invalid: negative
//        request.setCategoryId(category.getCategoryId());
//
//        mockMvc.perform(post("/api/v1/products")
//                .header("Authorization", "Bearer " + adminToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//            .andExpect(status().isBadRequest());
//    }
//
//    // ==================== PUT Tests (Admin Only) ====================
//
//    @Test
//    void updateProduct_AsAdmin_ReturnsUpdatedProduct() throws Exception {
//        UpdateProductRequest request = new UpdateProductRequest();
//        request.setProductName("Updated Laptop");
//        request.setDescription("Updated description");
//        request.setPrice(1200.0);
//        request.setQuantity(15);
//        request.setDiscount(15.0);
//
//        mockMvc.perform(put("/api/v1/products/{productId}", product.getProductId())
//                .header("Authorization", "Bearer " + adminToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.productName", is("Updated Laptop")))
//            .andExpect(jsonPath("$.price", is(1200.0)));
//    }
//
//    @Test
//    void updateProduct_NonExistentProduct_ReturnsNotFound() throws Exception {
//        UpdateProductRequest request = new UpdateProductRequest();
//        request.setProductName("Updated Laptop");
//        request.setDescription("Updated description");
//        request.setPrice(1200.0);
//        request.setQuantity(15);
//        request.setDiscount(15.0);
//
//        mockMvc.perform(put("/api/v1/products/{productId}", 9999L)
//                .header("Authorization", "Bearer " + adminToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//            .andExpect(status().isNotFound());
//    }
//
//    // ==================== DELETE Tests (Admin Only) ====================
//
//    @Test
//    void deleteProduct_AsAdmin_ReturnsSuccess() throws Exception {
//        mockMvc.perform(delete("/api/v1/products/{productId}", product.getProductId())
//                .header("Authorization", "Bearer " + adminToken)
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk());
//
//        // Verify product is deleted
//        mockMvc.perform(get("/api/v1/products/{productId}", product.getProductId())
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void deleteProduct_AsUser_ReturnsForbidden() throws Exception {
//        mockMvc.perform(delete("/api/v1/products/{productId}", product.getProductId())
//                .header("Authorization", "Bearer " + userToken)
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isForbidden());
//    }
//}
