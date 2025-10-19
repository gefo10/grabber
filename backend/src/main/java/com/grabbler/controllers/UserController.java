package com.grabbler.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grabbler.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.grabbler.models.User;

import com.grabbler.payloads.user.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "API Endpoints for user registration and management")
public class UserController {

    @Autowired
    private UserService userService;

    // test endpoint to get user details by id from JWT token
    @PreAuthorize("#userId == principal.userId")
    @GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUserById(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());

        UserDTO userDTO = userService.getUserById(user.getUserId());
        return ResponseEntity.ok(userDTO);
    }

}
