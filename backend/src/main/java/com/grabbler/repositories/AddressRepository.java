package com.grabbler.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grabbler.models.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("""
              SELECT a FROM Address a
              WHERE a.country = :country
                AND a.city = :city
                AND a.postalCode = :postalCode
                AND a.street = :street
            """)
    Optional<Address> findByCountryAndCityAndPostalCodeAndStreet(
            @Param("country") String country,
            @Param("city") String city,
            @Param("postalCode") String postalCode,
            @Param("street") String street);

    List<Address> findByCity(String city);
}
