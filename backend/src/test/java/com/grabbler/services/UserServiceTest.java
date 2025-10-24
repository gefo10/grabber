package com.grabbler.services;

import com.grabbler.payloads.user.*;
import org.junit.jupiter.api.BeforeEach;

public class UserServiceTest {

    @BeforeEach
    public void setUp() {
        UserDTO userDto = new UserDTO();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
    }
}
