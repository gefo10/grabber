package com.grabbler.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long productId;

  @NotBlank(message = "Name is mandatory")
  @Size(min = 3, message = "Name should have atleast 3 characters")
  private String productName;

  private String image;

  @NotBlank(message = "Description is mandatory")
  @Size(min = 10, message = "Description should have atleast 10 characters")
  private String description;

  private Integer quantity;
  private double price;
  private double discount;
  private double specialPrice;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  /**
   * Internal version field used for optimistic locking. Automatically incremented on updates; not
   * exposed publicly.
   */
  @Version private Long version;

  // Business constructor - only includes fields that should be set at creation
  public Product(
      String productName, String description, Integer quantity, double price, Category category) {
    this.productName = productName;
    this.description = description;
    this.quantity = quantity;
    this.price = price;
    this.category = category;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Product)) return false;
    Product product = (Product) o;
    return productId != null && productId.equals(product.productId);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Product{"
        + "productId="
        + productId
        + ", productName='"
        + productName
        + '\''
        + ", price="
        + price
        + ", quantity="
        + quantity
        + '}';
  }
}
