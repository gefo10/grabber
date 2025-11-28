package com.grabbler.repositories;

import com.grabbler.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  @Query("SELECT p FROM Product p WHERE p.productName LIKE %?1%")
  Page<Product> findByProductNameLike(String keyword, Pageable pageDetails);

  Page<Product> findAll(Pageable pageDetails);

  @Query("SELECT p FROM Product p WHERE p.category.categoryId = ?1")
  Page<Product> findByCategoryCategoryId(Long categoryId, Pageable pageDetails);

  @Query(
      "SELECT p FROM Product p WHERE p.productName LIKE %:keyword% OR p.description LIKE %:keyword%")
  Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

  @Query("Select p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
  Page<Product> findByPriceRange(
      @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.specialPrice BETWEEN :minPrice AND :maxPrice")
  Page<Product> findBySpecialPriceRange(
      @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.quantity > 0")
  Page<Product> findInStockProducts(Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.quantity = 0")
  Page<Product> findOutOfStockProducts(Pageable pageable);
}
