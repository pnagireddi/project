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

    public User update(User u) {
        if (u == null) throw new IllegalArgumentException("User must not be null");
        if (u.getUserId() == null) {
            // no id: treat as save
            if (u.getPasswordHash() != null && !u.getPasswordHash().isEmpty()) {
                u.setPasswordHash(passwordEncoder.encode(u.getPasswordHash()));
            }
            return userRepository.save(u);
        }
        Optional<User> opt = userRepository.findById(u.getUserId());
        if (opt.isPresent()) {
            User existing = opt.get();
            if (u.getUsername() != null) existing.setUsername(u.getUsername());
            if (u.getEmail() != null) existing.setEmail(u.getEmail());
            if (u.getRole() != null) existing.setRole(u.getRole());
            // preserve passwordHash if not provided; if provided assume raw password and encode
            if (u.getPasswordHash() != null && !u.getPasswordHash().isEmpty()) {
                existing.setPasswordHash(passwordEncoder.encode(u.getPasswordHash()));
            }
            return userRepository.save(existing);
        }
        // fallback to save incoming
        if (u.getPasswordHash() != null && !u.getPasswordHash().isEmpty()) {
            u.setPasswordHash(passwordEncoder.encode(u.getPasswordHash()));
        }
        return userRepository.save(u);
    }

    public void delete(Long id) { userRepository.deleteById(id); }

    public java.util.List<User> findAll() { return userRepository.findAll(); }
}
