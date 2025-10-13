package com.grabbler.services;

import com.grabbler.models.*;
import com.grabbler.payloads.*;
import com.grabbler.payloads.PaymentDTO;
import com.grabbler.repositories.*;
import org.junit.jupiter.api.BeforeEach;

public class UserServiceTest {

    @BeforeEach
    public void setUp() {
        UserDTO userDto = new UserDTO();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("password123");
        userDto.setAbout("A regular user");

    }
}
