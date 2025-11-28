package com.grabbler.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "addresses")
@AllArgsConstructor
@NoArgsConstructor
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long addressId;

  @NotBlank private String street;
  // private String addressLineTwo;
  private String additionalInfo;

  @NotBlank private String city;

  @NotBlank private String country;

  @NotBlank private String postalCode;

  @ManyToMany(mappedBy = "addresses")
  private List<User> users = new ArrayList<>();

  public Address(String country, String city, String plz, String street, String addressLineTwo) {
    this.country = country;
    this.city = city;
    this.postalCode = plz;
    this.street = street;
    // this.addressLineTwo = addressLineTwo;
  }
}
