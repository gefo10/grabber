package com.grabbler.repositories;

import com.grabbler.models.Category;
import com.grabbler.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = "com.grabbler.models")
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    // Remember to use the corrected method name here
    @Autowired
    private ProductRepository correctedProductRepository;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryName("Laptops");
        entityManager.persist(category);

        product = new Product();
        product.setProductName("Cool Laptop");
        product.setDescription("A very cool and fast laptop.");
        product.setPrice(1200.00);
        product.setQuantity(50);
        product.setCategory(category);
        entityManager.persist(product);
        entityManager.flush();
    }

    @Test
    public void whenFindByCategoryCategoryId_thenReturnProductPage() {
        // when
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> resultPage = correctedProductRepository.findByCategoryCategoryId(category.getCategoryId(),
                pageable);

        // then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent().get(0).getProductName()).isEqualTo("Cool Laptop");
    }

    @Test
    public void whenFindByProductNameLike_thenReturnProductPage() {
        // when
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> resultPage = productRepository.findByProductNameLike("%Laptop%", pageable);

        // then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent()).hasSize(1);
    }

    @Test
    public void whenFindByKeyword_inName_thenReturnProductPage() {
        // when
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> resultPage = productRepository.findByKeyword("cool", pageable);

        // then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent().get(0).getDescription()).contains("cool");
    }
}
