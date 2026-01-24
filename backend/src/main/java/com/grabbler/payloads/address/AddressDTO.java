package com.grabbler.payloads.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
