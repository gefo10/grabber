package com.grabbler.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grabbler.models.User;
import com.grabbler.payloads.user.UserDTO;
import com.grabbler.payloads.user.UserResponse;
import com.grabbler.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "API Endpoints for user registration and management")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get current user", description = "Returns the currently authenticated user's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserDTO userDTO = userService.getUserById(user.getUserId());
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Get user by ID", description = "Admin can get any user, users can only get themselves")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        if (userDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Update user", description = "Admin can update any user, users can only update themselves")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserDTO userDTO,
            Authentication authentication) {
        UserDTO updatedUser = userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete user", description = "Admin can delete any user, users can delete their own account")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        String message = userService.deleteUser(userId);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Get all users", description = "Admin only endpoint to retrieve all users")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<UserResponse> getAllUsers(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") Integer pageNumber,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") Integer pageSize,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "userId") String sortBy,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "asc") String sortOrder) {
        UserResponse userResponse = userService.getAllUsers(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(userResponse);
    }

}
