package com.grabbler.services;

import java.util.Optional;

import com.grabbler.models.User;
import com.grabbler.payloads.user.*;
import com.grabbler.payloads.user.UserCreateDTO;

public interface UserService {
    UserDTO registerUser(UserCreateDTO userCreateDTO);

    UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    UserDTO getUserById(Long userId);

    UserDTO updateUser(Long userId, UserDTO userDTO);

    String deleteUser(Long userId);

    Optional<User> findByEmail(String email);

    Optional<User> findUserById(Long userId);

}
