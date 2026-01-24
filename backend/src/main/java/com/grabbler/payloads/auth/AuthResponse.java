package com.grabbler.payloads.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.grabbler.payloads.user.UserDTO;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserDTO user;
}
