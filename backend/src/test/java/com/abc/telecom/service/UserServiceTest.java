package com.abc.telecom.service;

import com.abc.telecom.exception.UserAlreadyExistsException;
import com.abc.telecom.model.User;
import com.abc.telecom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void register_whenEmailExists_throws() {
        User u = new User(); u.setEmail("e@x.com");
        when(userRepository.existsByEmail("e@x.com")).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> userService.register(u));
        assertTrue(ex.getMessage().contains("Email already in use"));
    }

    @Test
    void register_whenUsernameExists_throws() {
        User u = new User(); u.setUsername("bob");
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername("bob")).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> userService.register(u));
        assertTrue(ex.getMessage().contains("Username already in use"));
    }

    @Test
    void register_happy_encodesAndSaves() {
        User u = new User(); u.setUsername("bob"); u.setPasswordHash("raw");
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(passwordEncoder.encode("raw")).thenReturn("enc");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User saved = userService.register(u);
        assertEquals("enc", saved.getPasswordHash());
        verify(userRepository).save(any());
    }

    @Test
    void findByUsername_delegates() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(new User()));
        assertTrue(userService.findByUsername("bob").isPresent());
    }

    @Test
    void findById_delegates() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(new User()));
        assertTrue(userService.findById(5L).isPresent());
    }

    @Test
    void update_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> userService.update(null));
    }

    @Test
    void update_withoutId_savesAndEncodesPassword() {
        User u = new User(); u.setPasswordHash("pw");
        when(passwordEncoder.encode("pw")).thenReturn("encpw");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User out = userService.update(u);
        assertEquals("encpw", out.getPasswordHash());
        verify(userRepository).save(any());
    }

    @Test
    void update_withId_present_updatesFieldsAndEncodes() {
        User existing = new User(); existing.setUserId(10L); existing.setUsername("old"); existing.setEmail("old@e"); existing.setRole("user");
        when(userRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newpw")).thenReturn("encnewpw");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User incoming = new User(); incoming.setUserId(10L); incoming.setUsername("n"); incoming.setEmail("n@e"); incoming.setRole("admin"); incoming.setPasswordHash("newpw");
        User out = userService.update(incoming);

        assertEquals(10L, out.getUserId());
        assertEquals("n", out.getUsername());
        assertEquals("n@e", out.getEmail());
        assertEquals("admin", out.getRole());
        assertEquals("encnewpw", out.getPasswordHash());
    }

    @Test
    void update_withId_missing_savesIncoming() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pw")) .thenReturn("epw");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User u = new User(); u.setUserId(99L); u.setPasswordHash("pw");
        User out = userService.update(u);
        assertEquals("epw", out.getPasswordHash());
    }

    @Test
    void delete_callsRepo() {
        doNothing().when(userRepository).deleteById(7L);
        userService.delete(7L);
        verify(userRepository).deleteById(7L);
    }

    @Test
    void findAll_delegates() {
        when(userRepository.findAll()).thenReturn(List.of(new User()));
        assertEquals(1, userService.findAll().size());
    }
}
