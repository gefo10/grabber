package com.grabbler.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grabbler.models.Address;
import com.grabbler.payloads.address.*;
import com.grabbler.services.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping("/address")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO address = addressService.createAddress(addressDTO);

        return new ResponseEntity<AddressDTO>(address, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses() {
        List<AddressDTO> addressDTOs = addressService.getAllAddresses();

        return new ResponseEntity<List<AddressDTO>>(addressDTOs, HttpStatus.FOUND);
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long addressId) {
        AddressDTO addressDTO = addressService.getAddressById(addressId);

        return new ResponseEntity<AddressDTO>(addressDTO, HttpStatus.FOUND);
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId, @Valid @RequestBody Address address) {
        AddressDTO addressDTO = addressService.updateAddress(addressId, address);

        return new ResponseEntity<AddressDTO>(addressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);

        return new ResponseEntity<String>("Address deleted successfully", HttpStatus.OK);
    }
}
