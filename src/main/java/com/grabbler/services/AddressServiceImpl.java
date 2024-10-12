package com.grabbler.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grabbler.models.Address;
import com.grabbler.payloads.AddressDTO;
import com.grabbler.repositories.AddressRepository;
import com.grabbler.repositories.UserRepository;
import org.modelmapper.ModelMapper;

@Service
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper ModelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAddress'");
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllAddresses'");
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAddressById'");
    }

    @Override
    public AddressDTO updateAddress(Long addressId, Address address) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAddress'");
    }

    @Override
    public String deleteAddress(Long addressId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAddress'");
    }

}
