package com.grabbler.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grabbler.payloads.AddressDTO;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/admin")
public class AddressController {
    @PostMapping("/address")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        //AddressDTO address = addressService.createAddress(addressDTO);
        //return ResponseEntity.ok(address);
        return null;
    }
}
