package com.grabbler.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
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

}
