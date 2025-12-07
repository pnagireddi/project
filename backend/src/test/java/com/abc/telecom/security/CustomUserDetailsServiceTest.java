package com.abc.telecom.security;

import com.abc.telecom.model.User;
import com.abc.telecom.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock
    UserRepository userRepo;

    @InjectMocks
    CustomUserDetailsService svc;

    @Test
    void loadUserByUsername_returns_userdetails(){
        User u = new User(); u.setUsername("x"); u.setPasswordHash("p"); u.setRole("CUSTOMER");
        when(userRepo.findByUsername("x")).thenReturn(Optional.of(u));
        UserDetails d = svc.loadUserByUsername("x");
        assertThat(d.getUsername()).isEqualTo("x");
    }

    @Test
    void missing_user_throws() {
        when(userRepo.findByUsername("nope")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> svc.loadUserByUsername("nope"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("nope");
    }
}
