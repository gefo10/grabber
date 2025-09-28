package com.grabbler.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grabbler.exceptions.APIException;
import com.grabbler.exceptions.ResourceNotFoundException;
import com.grabbler.models.Address;
import com.grabbler.models.User;
import com.grabbler.payloads.AddressDTO;
import com.grabbler.repositories.AddressRepository;
import com.grabbler.repositories.UserRepository;

import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;

@Transactional
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        String country = addressDTO.getCountry();
        String city = addressDTO.getCity();
        String addressLineOne = addressDTO.getAddressLineOne();
        // String addressLineTwo = addressDTO.getAddressLineTwo();
        // String additionalInfo = addressDTO.getAdditionalInfo();
        String plz = addressDTO.getPlz();

        Address addressFromDb = addressRepository.findByCountryAndCityAndPlzAndAddressLineOne(country, city, plz,
                addressLineOne);

        if (addressFromDb != null) {
            throw new APIException("Address already exists with addressId: " + addressFromDb.getAddressId());
        }

        Address address = modelMapper.map(addressDTO, Address.class);
        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);

    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        List<AddressDTO> addressDTOs = addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        return addressDTOs;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public AddressDTO updateAddress(Long addressId, Address address) {
        Address addressFromDb = addressRepository.findByCountryAndCityAndPlzAndAddressLineOne(address.getCountry(),
                address.getCity(), address.getPlz(), address.getAddressLineOne());

        if (addressFromDb == null) {
            addressFromDb = addressRepository.findById(addressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

            addressFromDb.setCountry(address.getCountry());
            addressFromDb.setCity(address.getCity());
            addressFromDb.setPlz(address.getPlz());
            addressFromDb.setAddressLineOne(address.getAddressLineOne());
            addressFromDb.setAddressLineTwo(address.getAddressLineTwo());
            addressFromDb.setAdditionalInfo(address.getAdditionalInfo());

            Address updatedAddress = addressRepository.save(addressFromDb);
            return modelMapper.map(updatedAddress, AddressDTO.class);
        } else {
            List<User> users = userRepository.findByAddress(addressFromDb.getAddressId());
            final Address a = addressFromDb;

            users.forEach(user -> {
                user.getAddresses().add(a);
            });

            deleteAddress(addressId);

            return modelMapper.map(addressFromDb, AddressDTO.class);
        }

    }

    @Override
    public String deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        List<User> users = userRepository.findByAddress(addressId);

        users.forEach(user -> {
            user.getAddresses().remove(address);
            userRepository.save(user);
        });
        addressRepository.deleteById(addressId);

        return "Address deleted successfully with addressId: " + addressId;
    }

}
