package com.grabbler.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grabbler.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.grabbler.payloads.user.*;

@RestController
@RequestMapping("/api")
@Tag(name = "User Management", description = "API Endpoints for user registration and management")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details.", tags = {
            "User Management" }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration details", required = true, content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserCreateDTO.class), examples = @ExampleObject(name = "User Registration Example", summary = "Example of user registration payload", value = """
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "jone.smith@example.com",
                        "password": "SecureP@ssw0rd",
                        "addresses": {
                            "street": "123 Main St",
                            "city": "Anytown",
                            "postalCode": "12345",
                            "country": "USA"
                        }
                    }
                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User with the given email already exists")
    })

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        UserDTO userDTO = userService.registerUser(userCreateDTO);

        URI location = URI.create(String.format("/api/users/%d", userDTO.getUserId()));
        String message = String.format("User '%s' registered successfully with ID: %d",
                userDTO.getFirstName() + " " + userDTO.getLastName(), userDTO.getUserId());
        return ResponseEntity.created(location).body(message);
    }
}
