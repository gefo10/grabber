package com.grabbler.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grabbler.models.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByCountryAndCityAndPlzAndAddressLineOne(String country, String city, String plz, String addressLineOne);
}
