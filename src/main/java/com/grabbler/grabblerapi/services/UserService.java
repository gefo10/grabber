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

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        return user.get();

    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        return user.get();
    }

    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        
        if(user.isEmpty()) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }
        return user.get();
    }

}
