package com.grabbler.payloads.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long addressId;
    private String street;
    private String additionalInfo;
    private String city;
    private String country;
    private String postalCode;
    private String houseNumber;
}
