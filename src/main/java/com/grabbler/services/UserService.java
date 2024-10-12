package com.grabbler.services;

import com.grabbler.payloads.UserDTO;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);

    //TODO: Get all users 
    
    UserDTO getUserById(Long userId);

    UserDTO updateUser(Long userId, UserDTO userDTO);

    String deleteUser(Long userId);
}
