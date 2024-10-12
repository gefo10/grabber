package com.grabbler.services;

import java.util.List;

import com.grabbler.models.Address;
import com.grabbler.payloads.AddressDTO;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    AddressDTO updateAddress(Long addressId, Address address);

    String deleteAddress(Long addressId);
}
