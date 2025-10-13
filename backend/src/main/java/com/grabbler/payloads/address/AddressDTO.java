package com.grabbler.payloads.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long addressId;
    private String addressLineOne;
    private String addressLineTwo;
    private String additionalInfo;
    private String city;
    private String country;
    private String plz;
    private String house;
}
