package com.grabbler.testutils;

import com.grabbler.enums.OrderStatus;
import com.grabbler.enums.PaymentMethod;
import com.grabbler.enums.PaymentStatus;
import com.grabbler.models.*;
import com.grabbler.payloads.payment.PaymentDTO;
import com.grabbler.payloads.product.CreateProductRequest;
import com.grabbler.payloads.user.UserCreateDTO;
import com.grabbler.payloads.address.AddressDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test Utilities and Data Builders for Spring Boot Testing
 * 
 * This class demonstrates best practices for creating test data:
 * 1. Builder pattern for complex objects
 * 2. Sensible defaults with customization options
 * 3. Reusable across multiple test classes
 * 4. Type-safe and fluent API
 * 
 * BEST PRACTICES FOR SPRING BOOT TESTING:
 * 
 * 1. TEST ORGANIZATION
 * - Use @Nested classes to group related tests
 * - Follow naming: methodName_scenario_expectedBehavior
 * - Use @DisplayName for readable test descriptions
 * 
 * 2. TEST DATA MANAGEMENT
 * - Use builders (like this class) for complex objects
 * - Create fresh data for each test (@BeforeEach)
 * - Clean up after tests (especially in integration tests)
 * 
 * 3. UNIT VS INTEGRATION TESTS
 * Unit Tests:
 * - @ExtendWith(MockitoExtension.class)
 * - Mock all dependencies
 * - Fast, isolated
 * 
 * Integration Tests:
 * - @SpringBootTest
 * - @AutoConfigureMockMvc
 * - Use real Spring context
 * - Test database interactions
 * 
 * 4. DATABASE TESTING
 * - Use @Transactional to rollback after each test
 * - Use H2 in-memory database for tests
 * - Use @DataJpaTest for repository tests
 * 
 * 5. MOCKING STRATEGIES
 * - Mock external services
 * - Don't mock what you're testing
 * - Use @MockBean for Spring beans in integration tests
 * - Use @Mock for unit tests
 * 
 * 6. ASSERTIONS
 * - Use AssertJ for fluent assertions
 * - One logical assertion per test
 * - Test both success and failure cases
 * 
 * 7. TEST COVERAGE
 * - Aim for 80%+ code coverage
 * - Focus on business logic
 * - Don't forget edge cases and exceptions
 */
public class TestDataBuilder {

    // ==================== User Builders ====================

    public static class UserBuilder {
        private String firstName = "Test";
        private String lastName = "User";
        private String email = "test@example.com";
        private String password = "password123";
        private List<Role> roles = new ArrayList<>();
        private List<Address> addresses = new ArrayList<>();

        public UserBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder withRole(Role role) {
            this.roles.add(role);
            return this;
        }

        public UserBuilder withAddress(Address address) {
            this.addresses.add(address);
            return this;
        }

        public User build() {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(password);
            user.getRoles().addAll(roles);
            user.setAddresses(addresses);
            return user;
        }
    }

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    // ==================== Product Builders ====================

    public static class ProductBuilder {
        private String productName = "Test Product";
        private String description = "Test Description";
        private Double price = 100.0;
        private Integer quantity = 10;
        private Double discount = 0.0;
        private Category category;
        private String image = "test-image.jpg";

        public ProductBuilder withName(String name) {
            this.productName = name;
            return this;
        }

        public ProductBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder withPrice(Double price) {
            this.price = price;
            return this;
        }

        public ProductBuilder withQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public ProductBuilder withDiscount(Double discount) {
            this.discount = discount;
            return this;
        }

        public ProductBuilder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public ProductBuilder withImage(String image) {
            this.image = image;
            return this;
        }

        public Product build() {
            Product product = new Product();
            product.setProductName(productName);
            product.setDescription(description);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setDiscount(discount);
            product.setCategory(category);
            product.setImage(image);

            // Calculate special price
            double specialPrice = price - (price * discount / 100);
            product.setSpecialPrice(specialPrice);

            return product;
        }

        public CreateProductRequest buildRequest() {
            CreateProductRequest request = new CreateProductRequest();
            request.setProductName(productName);
            request.setDescription(description);
            request.setPrice(price);
            request.setQuantity(quantity);
            request.setDiscount(discount);
            if (category != null) {
                request.setCategoryId(category.getCategoryId());
            }
            return request;
        }
    }

    public static ProductBuilder aProduct() {
        return new ProductBuilder();
    }

    // ==================== Order Builders ====================

    public static class OrderBuilder {
        private User user;
        private LocalDate orderDate = LocalDate.now();
        private Double totalAmount = 100.0;
        private OrderStatus orderStatus = OrderStatus.PENDING;
        private Payment payment;
        private List<OrderItem> orderItems = new ArrayList<>();

        public OrderBuilder forUser(User user) {
            this.user = user;
            return this;
        }

        public OrderBuilder withDate(LocalDate date) {
            this.orderDate = date;
            return this;
        }

        public OrderBuilder withTotalAmount(Double amount) {
            this.totalAmount = amount;
            return this;
        }

        public OrderBuilder withStatus(OrderStatus status) {
            this.orderStatus = status;
            return this;
        }

        public OrderBuilder withPayment(Payment payment) {
            this.payment = payment;
            return this;
        }

        public OrderBuilder withOrderItem(OrderItem item) {
            this.orderItems.add(item);
            return this;
        }

        public Order build() {
            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(orderDate);
            order.setTotalAmount(totalAmount);
            order.setOrderStatus(orderStatus);
            order.setPayment(payment);
            order.setOrderItems(orderItems);
            return order;
        }
    }

    public static OrderBuilder anOrder() {
        return new OrderBuilder();
    }

    // ==================== Cart Builders ====================

    public static class CartBuilder {
        private User user;
        private Double totalPrice = 0.0;
        private List<CartItem> cartItems = new ArrayList<>();

        public CartBuilder forUser(User user) {
            this.user = user;
            return this;
        }

        public CartBuilder withTotalPrice(Double price) {
            this.totalPrice = price;
            return this;
        }

        public CartBuilder withCartItem(CartItem item) {
            this.cartItems.add(item);
            return this;
        }

        public Cart build() {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setTotalPrice(totalPrice);
            cart.setCartItems(cartItems);
            return cart;
        }
    }

    public static CartBuilder aCart() {
        return new CartBuilder();
    }

    // ==================== Payment Builders ====================

    public static class PaymentBuilder {
        private PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        private String paymentToken = "test_token_123";
        private PaymentStatus paymentStatus = PaymentStatus.PENDING;

        public PaymentBuilder withMethod(PaymentMethod method) {
            this.paymentMethod = method;
            return this;
        }

        public PaymentBuilder withToken(String token) {
            this.paymentToken = token;
            return this;
        }

        public PaymentBuilder withStatus(PaymentStatus status) {
            this.paymentStatus = status;
            return this;
        }

        public Payment build() {
            Payment payment = new Payment();
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentToken(paymentToken);
            payment.setPaymentStatus(paymentStatus);
            return payment;
        }

        public PaymentDTO buildDTO() {
            PaymentDTO dto = new PaymentDTO();
            dto.setPaymentMethod(paymentMethod);
            dto.setPaymentToken(paymentToken);
            dto.setPaymentStatus(paymentStatus);
            return dto;
        }
    }

    public static PaymentBuilder aPayment() {
        return new PaymentBuilder();
    }

    // ==================== Category Builders ====================

    public static class CategoryBuilder {
        private String categoryName = "Test Category";
        private List<Product> products = new ArrayList<>();

        public CategoryBuilder withName(String name) {
            this.categoryName = name;
            return this;
        }

        public CategoryBuilder withProduct(Product product) {
            this.products.add(product);
            return this;
        }

        public Category build() {
            Category category = new Category();
            category.setCategoryName(categoryName);
            category.setProducts(products);
            return category;
        }
    }

    public static CategoryBuilder aCategory() {
        return new CategoryBuilder();
    }

    // ==================== Address Builders ====================

    public static class AddressBuilder {
        private String street = "123 Test Street";
        private String city = "Test City";
        private String postalCode = "12345";
        private String country = "Test Country";
        private String additionalInfo = null;

        public AddressBuilder withStreet(String street) {
            this.street = street;
            return this;
        }

        public AddressBuilder withCity(String city) {
            this.city = city;
            return this;
        }

        public AddressBuilder withPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public AddressBuilder withCountry(String country) {
            this.country = country;
            return this;
        }

        public AddressBuilder withAdditionalInfo(String info) {
            this.additionalInfo = info;
            return this;
        }

        public Address build() {
            Address address = new Address();
            address.setStreet(street);
            address.setCity(city);
            address.setPostalCode(postalCode);
            address.setCountry(country);
            address.setAdditionalInfo(additionalInfo);
            return address;
        }

        public AddressDTO buildDTO() {
            AddressDTO dto = new AddressDTO();
            dto.setStreet(street);
            dto.setCity(city);
            dto.setPostalCode(postalCode);
            dto.setCountry(country);
            dto.setAdditionalInfo(additionalInfo);
            return dto;
        }
    }

    public static AddressBuilder anAddress() {
        return new AddressBuilder();
    }

    // ==================== Role Builder ====================

    public static class RoleBuilder {
        private String roleName = "ROLE_CUSTOMER";

        public RoleBuilder withName(String name) {
            this.roleName = name;
            return this;
        }

        public RoleBuilder admin() {
            this.roleName = "ROLE_ADMIN";
            return this;
        }

        public RoleBuilder customer() {
            this.roleName = "ROLE_CUSTOMER";
            return this;
        }

        public Role build() {
            Role role = new Role();
            role.setRoleName(roleName);
            return role;
        }
    }

    public static RoleBuilder aRole() {
        return new RoleBuilder();
    }

    // ==================== Usage Examples ====================

    /**
     * Example usage in tests:
     * 
     * // Simple usage with defaults
     * User user = TestDataBuilder.aUser().build();
     * 
     * // Customized user
     * User admin = TestDataBuilder.aUser()
     * .withEmail("admin@test.com")
     * .withRole(TestDataBuilder.aRole().admin().build())
     * .build();
     * 
     * // Product with category
     * Category category = TestDataBuilder.aCategory()
     * .withName("Electronics")
     * .build();
     * 
     * Product product = TestDataBuilder.aProduct()
     * .withName("Laptop")
     * .withPrice(1000.0)
     * .withCategory(category)
     * .build();
     * 
     * // Complete order scenario
     * User customer = TestDataBuilder.aUser().build();
     * Payment payment = TestDataBuilder.aPayment().build();
     * Order order = TestDataBuilder.anOrder()
     * .forUser(customer)
     * .withPayment(payment)
     * .withStatus(OrderStatus.PENDING)
     * .build();
     */

    // ==================== Additional Test Utilities ====================

    /**
     * Generate random email for unique users in tests
     */
    public static String randomEmail() {
        return "test" + System.currentTimeMillis() + "@example.com";
    }

    /**
     * Generate random product name
     */
    public static String randomProductName() {
        return "Product-" + System.currentTimeMillis();
    }

    /**
     * Create a complete test scenario with user, cart, and products
     */
    public static TestScenario createShoppingScenario() {
        return new TestScenario();
    }

    public static class TestScenario {
        private User user;
        private Cart cart;
        private List<Product> products = new ArrayList<>();
        private Category category;

        public TestScenario() {
            // Create category
            this.category = aCategory().withName("Electronics").build();

            // Create products
            for (int i = 1; i <= 3; i++) {
                Product product = aProduct()
                        .withName("Product " + i)
                        .withPrice(100.0 * i)
                        .withCategory(category)
                        .build();
                products.add(product);
            }

            // Create user
            this.user = aUser()
                    .withEmail(randomEmail())
                    .build();

            // Create cart
            this.cart = aCart()
                    .forUser(user)
                    .build();
        }

        public User getUser() {
            return user;
        }

        public Cart getCart() {
            return cart;
        }

        public List<Product> getProducts() {
            return products;
        }

        public Category getCategory() {
            return category;
        }
    }
}

/**
 * TESTING CHEAT SHEET:
 * 
 * 1. MOCKITO ANNOTATIONS:
 * 
 * @Mock - Creates a mock
 * @InjectMocks - Injects mocks into the class
 * @Captor - Captures arguments passed to mocks
 * @Spy - Partial mocking (real methods with some mocked)
 * 
 *      2. MOCKITO METHODS:
 *      when(mock.method()).thenReturn(value) - Stub behavior
 *      verify(mock).method() - Verify interaction
 *      verify(mock, times(2)).method() - Verify call count
 *      verify(mock, never()).method() - Verify never called
 *      any(), anyLong(), anyString() - Argument matchers
 * 
 *      3. ASSERTIONS:
 *      assertEquals(expected, actual)
 *      assertNotNull(value)
 *      assertTrue(condition)
 *      assertThrows(Exception.class, () -> code)
 *      assertAll(() -> assertion1, () -> assertion2)
 * 
 *      4. MOCKMVC:
 *      mockMvc.perform(get("/api/endpoint"))
 *      .andExpect(status().isOk())
 *      .andExpect(jsonPath("$.field", is(value)))
 *      .andExpect(content().string(containsString("text")))
 * 
 *      5. TEST LIFECYCLE:
 * @BeforeAll - Once before all tests (static)
 * @BeforeEach - Before each test
 * @AfterEach - After each test
 * @AfterAll - Once after all tests (static)
 * 
 *           6. SPRING TEST ANNOTATIONS:
 * @SpringBootTest - Full application context
 *                 @WebMvcTest(Controller.class) - Only web layer
 * @DataJpaTest - Only JPA components
 * @AutoConfigureMockMvc - Auto-configure MockMvc
 * @MockBean - Mock a Spring bean
 * @TestConfiguration - Additional test configuration
 * 
 *                    7. TRANSACTION MANAGEMENT:
 * @Transactional - Rollback after test
 * @Commit - Commit changes (rare in tests)
 * @Rollback - Explicit rollback
 */
