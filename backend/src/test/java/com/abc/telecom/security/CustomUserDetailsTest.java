package com.abc.telecom.security;

import com.abc.telecom.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomUserDetailsTest {

    @Test
    void basic_userdetails_mapping() {
        User u = new User();
        u.setUserId(10L);
        u.setUsername("jdoe");
        u.setPasswordHash("secret");
        u.setRole("customer");

        CustomUserDetails d = new CustomUserDetails(u);

        assertThat(d.getUsername()).isEqualTo("jdoe");
        assertThat(d.getPassword()).isEqualTo("secret");
        assertThat(d.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        assertThat(d.isAccountNonExpired()).isTrue();
        assertThat(d.isAccountNonLocked()).isTrue();
        assertThat(d.isCredentialsNonExpired()).isTrue();
        assertThat(d.isEnabled()).isTrue();
    }
}
