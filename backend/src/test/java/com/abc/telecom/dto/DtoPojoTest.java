package com.abc.telecom.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DtoPojoTest {
    private final ObjectMapper M = new ObjectMapper();

    @Test
    void registerRequest_gettersSetters() {
        RegisterRequest r = new RegisterRequest();
        r.setUsername("u"); r.setPassword("p"); r.setEmail("e"); r.setRole("customer");
        assertEquals("u", r.getUsername());
        assertEquals("p", r.getPassword());
        assertEquals("e", r.getEmail());
        assertEquals("customer", r.getRole());
    }

    @Test
    void authRequest_and_response_serialization() throws Exception {
        AuthRequest a = new AuthRequest(); a.setUsername("bob"); a.setPassword("pw");
        assertEquals("bob", a.getUsername());

        AuthResponse resp = new AuthResponse("tok");
        String json = M.writeValueAsString(resp);
        AuthResponse parsed = M.readValue(json, AuthResponse.class);
        assertEquals("tok", parsed.getToken());
    }

    @Test
    void simple_dto_fields() {
        PaymentDto p = new PaymentDto(); p.setPaymentId(1L); p.setAmount(2.5);
        assertEquals(1L, p.getPaymentId());
        assertEquals(2.5, p.getAmount());

        InvoiceDto i = new InvoiceDto(); i.setInvoiceId(3L); i.setTotalAmount(100.0);
        assertEquals(3L, i.getInvoiceId());
        assertEquals(100.0, i.getTotalAmount());

        CustomerDto c = new CustomerDto(); c.setCustomerId(4L); c.setFullName("Name");
        assertEquals(4L, c.getCustomerId());
        assertEquals("Name", c.getFullName());

        CreateServiceDto s = new CreateServiceDto(); s.setCustomerId(20L); s.setServiceType("voice");
        assertEquals(20L, s.getCustomerId());
        assertEquals("voice", s.getServiceType());

        UsageDto u = new UsageDto(); u.setUsageId(55L); u.setUsageAmount(12.0); u.setUsageDate(LocalDateTime.now());
        assertEquals(55L, u.getUsageId());
    }
}
