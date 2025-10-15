package com.grabbler.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grabbler.services.UserService;

import jakarta.validation.Valid;

import com.grabbler.payloads.user.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        UserDTO userDTO = userService.registerUser(userCreateDTO);

        URI location = URI.create(String.format("/api/users/%d", userDTO.getUserId()));
        String message = String.format("User '%s' registered successfully with ID: %d",
                userDTO.getFirstName() + " " + userDTO.getLastName(), userDTO.getUserId());
        return ResponseEntity.created(location).body(message);
    }
}
