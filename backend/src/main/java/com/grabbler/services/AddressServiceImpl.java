package com.grabbler.services;

import com.grabbler.exceptions.APIException;
import com.grabbler.exceptions.ResourceNotFoundException;
import com.grabbler.models.Address;
import com.grabbler.models.User;
import com.grabbler.payloads.address.*;
import com.grabbler.repositories.AddressRepository;
import com.grabbler.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class AddressServiceImpl implements AddressService {

  @Autowired private AddressRepository addressRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private ModelMapper modelMapper;

  @Override
  public AddressDTO createAddress(AddressDTO addressDTO) {
    String country = addressDTO.getCountry();
    String city = addressDTO.getCity();
    String street = addressDTO.getStreet();
    // String addressLineTwo = addressDTO.getAddressLineTwo();
    // String additionalInfo = addressDTO.getAdditionalInfo();
    String plz = addressDTO.getPostalCode();

    Optional<Address> addressFromDb =
        addressRepository.findByCountryAndCityAndPostalCodeAndStreet(country, city, plz, street);

    if (addressFromDb.isPresent()) {
      throw new APIException(
          "Address already exists with addressId: " + addressFromDb.get().getAddressId());
    }

    Address address = modelMapper.map(addressDTO, Address.class);
    Address savedAddress = addressRepository.save(address);

    return modelMapper.map(savedAddress, AddressDTO.class);
  }

  @Override
  public List<AddressDTO> getAllAddresses() {
    List<Address> addresses = addressRepository.findAll();
    List<AddressDTO> addressDTOs =
        addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();

    return addressDTOs;
  }

  @Override
  public AddressDTO getAddressById(Long addressId) {
    Address address =
        addressRepository
            .findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
    return modelMapper.map(address, AddressDTO.class);
  }

  @Override
  public AddressDTO updateAddress(Long addressId, Address address) {
    Optional<Address> addressFromDb =
        addressRepository.findByCountryAndCityAndPostalCodeAndStreet(
            address.getCountry(), address.getCity(), address.getPostalCode(), address.getStreet());

    if (addressFromDb.isEmpty() || addressFromDb.get().getAddressId().equals(addressId)) {
      addressFromDb = addressRepository.findById(addressId);

      if (addressFromDb.isEmpty()) {
        throw new ResourceNotFoundException("Address", "addressId", addressId);
      }

      Address addressToUpdate = addressFromDb.get();

      addressToUpdate.setCountry(address.getCountry());
      addressToUpdate.setCity(address.getCity());
      addressToUpdate.setPostalCode(address.getPostalCode());
      addressToUpdate.setStreet(address.getStreet());
      // addressFromDb.setAddressLineTwo(address.getAddressLineTwo());
      addressToUpdate.setAdditionalInfo(address.getAdditionalInfo());

      Address updatedAddress = addressRepository.save(addressToUpdate);
      return modelMapper.map(updatedAddress, AddressDTO.class);
    } else {
      List<User> users = userRepository.findByAddress(addressFromDb.get().getAddressId());
      final Address a = addressFromDb.get();

      users.forEach(
          user -> {
            user.getAddresses().add(a);
          });

      deleteAddress(addressId);

      return modelMapper.map(addressFromDb, AddressDTO.class);
    }
  }

  @Override
  public String deleteAddress(Long addressId) {
    Address address =
        addressRepository
            .findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

    List<User> users = userRepository.findByAddress(addressId);

    users.forEach(
        user -> {
          user.getAddresses().remove(address);
          userRepository.save(user);
        });
    addressRepository.deleteById(addressId);

    return "Address deleted successfully with addressId: " + addressId;
  }
}
