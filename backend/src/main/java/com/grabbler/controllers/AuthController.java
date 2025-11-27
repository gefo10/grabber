package com.grabbler.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grabbler.models.User;
import com.grabbler.payloads.auth.AuthRequest;
import com.grabbler.payloads.user.UserCreateDTO;
import com.grabbler.payloads.user.UserDTO;
import com.grabbler.security.JwtUtil;
import com.grabbler.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.models.media.Content;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details.", tags = {
            "User Management" }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration details", required = true, content = @io.swagger.v3.oas.annotations.media.content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserCreateDTO.class), examples = @ExampleObject(name = "User Registration Example", summary = "Example of user registration payload", value = """
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
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        UserDTO userDTO = userService.registerUser(userCreateDTO);

        URI location = URI.create(String.format("/api/users/%d", userDTO.getUserId()));
        String message = String.format("User '%s' registered successfully with ID: %d",
                userDTO.getFirstName() + " " + userDTO.getLastName(), userDTO.getUserId());
        return ResponseEntity.created(location).body(message);
    }

    @Operation(summary = "Login as registered user", description = "Authenticates a registed user with email and password", tags = {
            "User Management" }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Authentication Details", required = true, content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthRequest.class), examples = @ExampleObject(name = "User Registration Example", summary = "Example of user registration payload", value = """
                    {
                        "email": "john.smith@example.com",
                        "password": "SecureP@ssw0rd"
                    }
                    """))))
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()));

            User user = (User) auth.getPrincipal();

            System.out.println("User auth: " + user.getEmail() + " Password " + user.getPassword());
            System.out.println("User roles: " + user.getAuthorities());

            String username = user.getUsername();
            String email = user.getEmail();
            String userId = user.getUserId().toString();
            List<String> roles = user.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .collect(Collectors.toList());

            System.out.println("About to generate jwt");
            final String jwt = jwtUtil.generateToken(username, email, roles, userId);
            System.out.println("JWT generated: " + jwt);

            return ResponseEntity.ok().body("{\"token\": \"" + jwt + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

}
