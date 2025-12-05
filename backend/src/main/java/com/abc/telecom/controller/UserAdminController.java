package com.abc.telecom.controller;

import com.abc.telecom.model.User;
import com.abc.telecom.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Admin Users", description = "Admin operations for user management")
public class UserAdminController {
    private final UserService userService;

    public UserAdminController(UserService userService) { this.userService = userService; }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get user by id (admin only)")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update user by id (admin only)")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User u) {
        return userService.findById(id).map(existing -> {
            u.setUserId(id);
            return ResponseEntity.ok(userService.update(u));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id (admin only)")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
