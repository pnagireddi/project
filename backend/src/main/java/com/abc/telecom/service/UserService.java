package com.abc.telecom.service;

import com.abc.telecom.model.User;
import com.abc.telecom.repository.UserRepository;
import com.abc.telecom.exception.UserAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        // Check uniqueness before attempting to save to provide a clear 409 response
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use: " + user.getEmail());
        }
        if (user.getUsername() != null && userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already in use: " + user.getUsername());
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) { return userRepository.findById(id); }

    public User update(User u) { return userRepository.save(u); }

    public void delete(Long id) { userRepository.deleteById(id); }
}
