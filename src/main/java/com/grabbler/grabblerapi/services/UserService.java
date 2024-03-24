package com.grabbler.grabblerapi.services;

import com.grabbler.grabblerapi.model.User;
import com.grabbler.grabblerapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
    }
}
