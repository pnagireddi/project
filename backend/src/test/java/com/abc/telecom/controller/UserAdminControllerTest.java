package com.abc.telecom.controller;

import com.abc.telecom.model.User;
import com.abc.telecom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserAdminControllerTest {
    UserService userService;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        userService = Mockito.mock(UserService.class);
        UserAdminController controller = new UserAdminController(userService);
        mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getUser_found_returnsUser() throws Exception {
        User u = new User(); u.setUserId(5L); u.setUsername("alice");
        when(userService.findById(5L)).thenReturn(Optional.of(u));

        mvc.perform(get("/api/users/5")).andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(5));
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        when(userService.findById(7L)).thenReturn(Optional.empty());
        mvc.perform(get("/api/users/7")).andExpect(status().isNotFound());
    }

    @Test
    void updateUser_found_updates() throws Exception {
        User existing = new User(); existing.setUserId(10L);
        when(userService.findById(10L)).thenReturn(Optional.of(existing));
        User updated = new User(); updated.setUserId(10L); updated.setUsername("newname");
        when(userService.update(any())).thenReturn(updated);

        String body = "{ \"username\": \"newname\" }";
        mvc.perform(put("/api/users/10").contentType("application/json").content(body)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("newname"));
    }

    @Test
    void deleteUser_callsDelete() throws Exception {
        mvc.perform(delete("/api/users/11").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
        verify(userService).delete(11L);
    }

    @Test
    void listUsers_returnsList() throws Exception {
        User u = new User(); u.setUserId(2L); u.setUsername("u2");
        when(userService.findAll()).thenReturn(List.of(u));
        mvc.perform(get("/api/users")).andExpect(status().isOk()).andExpect(jsonPath("$[0].userId").value(2));
    }
}
