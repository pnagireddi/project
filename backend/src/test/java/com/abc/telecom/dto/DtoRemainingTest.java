package com.abc.telecom.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DtoRemainingTest {

    @Test
    void register_request_getters_setters() {
        RegisterRequest r = new RegisterRequest();
        r.setUsername("newuser");
        r.setPassword("p");
        r.setEmail("a@b.com");
        r.setRole("CUSTOMER");

        assertThat(r.getUsername()).isEqualTo("newuser");
        assertThat(r.getPassword()).isEqualTo("p");
        assertThat(r.getEmail()).isEqualTo("a@b.com");
        assertThat(r.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    void customer_dto_getters_setters() {
        CustomerDto c = new CustomerDto();
        c.setCustomerId(11L);
        c.setUserId(22L);
        c.setFullName("Jane Doe");
        c.setAddress("123 Main St");
        c.setPhoneNumber("555-0100");

        assertThat(c.getCustomerId()).isEqualTo(11L);
        assertThat(c.getUserId()).isEqualTo(22L);
        assertThat(c.getFullName()).isEqualTo("Jane Doe");
        assertThat(c.getAddress()).isEqualTo("123 Main St");
        assertThat(c.getPhoneNumber()).isEqualTo("555-0100");
    }
}
