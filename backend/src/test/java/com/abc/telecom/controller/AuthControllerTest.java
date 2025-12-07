package com.abc.telecom.controller;

import com.abc.telecom.dto.RegisterRequest;
import com.abc.telecom.model.User;
import com.abc.telecom.model.Customer;
import com.abc.telecom.service.CustomerService;
import com.abc.telecom.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthControllerTest {
    MockMvc mvc;

    @org.mockito.Mock
    UserService userService;
    @org.mockito.Mock
    CustomerService customerService;
    @org.mockito.Mock
    org.springframework.security.authentication.AuthenticationManager authenticationManager;
    @org.mockito.Mock
    com.abc.telecom.security.JwtUtil jwtUtil;
    @org.mockito.Mock
    com.abc.telecom.security.CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setup() {
        org.mockito.MockitoAnnotations.openMocks(this);
        AuthController controller = new AuthController(userService, authenticationManager, jwtUtil, customerService);
        mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void register_creates_user_and_customer() throws Exception {
        User u = new User(); u.setUserId(11L); u.setUsername("bob"); u.setRole("customer");
        Customer c = new Customer(); c.setCustomerId(22L);
        when(userService.register(any())).thenReturn(u);
        when(customerService.save(any())).thenReturn(c);

        String json = "{\"username\":\"bob\",\"password\":\"p\",\"email\":\"b@example.com\",\"role\":\"customer\"}";
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json)
            .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(status().isOk());
    }

    @Test
    void login_success_returnsToken() throws Exception {
        when(authenticationManager.authenticate(any())).thenReturn(Mockito.mock(org.springframework.security.core.Authentication.class));
        when(jwtUtil.generateToken("bob")).thenReturn("tok-abc");

        String json = "{\"username\":\"bob\",\"password\":\"p\"}";
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("tok-abc"));
    }

    @Test
    void login_failure_authExceptionResultsInServerError() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new org.springframework.security.authentication.BadCredentialsException("bad creds"));

        String json = "{\"username\":\"bad\",\"password\":\"x\"}";
        org.junit.jupiter.api.Assertions.assertThrows(org.springframework.security.authentication.BadCredentialsException.class, () -> {
            try {
                mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                        .andReturn();
            } catch (Exception e) {
                Throwable root = e;
                while (root.getCause() != null) root = root.getCause();
                if (root instanceof org.springframework.security.authentication.BadCredentialsException) {
                    throw (org.springframework.security.authentication.BadCredentialsException) root;
                }
                throw e;
            }
        });
    }
}
