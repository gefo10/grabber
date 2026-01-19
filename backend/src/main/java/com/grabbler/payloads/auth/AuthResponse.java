package com.grabbler.payloads.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.grabbler.payloads.user.UserDTO;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserDTO user;
}
