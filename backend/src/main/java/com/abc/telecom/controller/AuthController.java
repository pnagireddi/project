package com.abc.telecom.controller;

import com.abc.telecom.dto.AuthRequest;
import com.abc.telecom.dto.AuthResponse;
import com.abc.telecom.dto.RegisterRequest;
import com.abc.telecom.model.User;
import com.abc.telecom.security.JwtUtil;
import com.abc.telecom.service.UserService;
import com.abc.telecom.service.CustomerService;
import com.abc.telecom.model.Customer;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomerService customerService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomerService customerService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.customerService = customerService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterRequest.class), examples = {
            @ExampleObject(name = "example", value = "{\"username\": \"user_example\", \"password\": \"P@ssw0rd123\", \"email\": \"user+unique@example.com\", \"role\": \"CUSTOMER\"}")
    }))
    public ResponseEntity<?> register(@org.springframework.web.bind.annotation.RequestBody RegisterRequest req) {
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPasswordHash(req.getPassword());
        u.setEmail(req.getEmail());
        u.setRole(req.getRole() == null ? "customer" : req.getRole());
        User saved = userService.register(u);

        // If role is customer, create a linked Customer profile automatically
        Customer createdCustomer = null;
        if (saved.getRole() != null && saved.getRole().equalsIgnoreCase("customer")) {
            Customer c = new Customer();
            c.setUserId(saved.getUserId());
            c.setFullName(saved.getUsername());
            createdCustomer = customerService.save(c);
        }

        if (createdCustomer != null) {
            return ResponseEntity.ok(Map.of("user", saved, "customer", createdCustomer));
        }
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive a JWT token")
    @RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthRequest.class), examples = {
            @ExampleObject(name = "example", value = "{\"username\": \"user_example\", \"password\": \"P@ssw0rd123\"}")
    }))
    public ResponseEntity<?> login(@org.springframework.web.bind.annotation.RequestBody AuthRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.generateToken(req.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
