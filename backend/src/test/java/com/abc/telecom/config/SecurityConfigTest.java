package com.abc.telecom.config;

import com.abc.telecom.security.CustomUserDetailsService;
import com.abc.telecom.security.JwtAuthFilter;
import com.abc.telecom.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityConfigTest {
    @Test
    void passwordEncoder_is_bcrypt() {
        SecurityConfig cfg = new SecurityConfig(Mockito.mock(CustomUserDetailsService.class), Mockito.mock(JwtAuthFilter.class));
        assertThat(cfg.passwordEncoder()).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void authenticationProvider_wires_userDetails_and_passwordEncoder() {
        CustomUserDetailsService uds = Mockito.mock(CustomUserDetailsService.class);
        SecurityConfig cfg = new SecurityConfig(uds, Mockito.mock(JwtAuthFilter.class));
        DaoAuthenticationProvider provider = cfg.authenticationProvider();
        try {
            java.lang.reflect.Field udsField = DaoAuthenticationProvider.class.getDeclaredField("userDetailsService");
            udsField.setAccessible(true);
            Object udsValue = udsField.get(provider);
            assertThat(udsValue).isSameAs(uds);

            java.lang.reflect.Field peField = DaoAuthenticationProvider.class.getDeclaredField("passwordEncoder");
            peField.setAccessible(true);
            Object peValue = peField.get(provider);
            assertThat(peValue).isInstanceOf(BCryptPasswordEncoder.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void authenticationManager_delegates_to_configuration() throws Exception {
        SecurityConfig cfg = new SecurityConfig(Mockito.mock(CustomUserDetailsService.class), Mockito.mock(JwtAuthFilter.class));
        AuthenticationConfiguration authConfig = Mockito.mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = Mockito.mock(AuthenticationManager.class);
        Mockito.when(authConfig.getAuthenticationManager()).thenReturn(manager);
        assertThat(cfg.authenticationManager(authConfig)).isSameAs(manager);
    }
}
