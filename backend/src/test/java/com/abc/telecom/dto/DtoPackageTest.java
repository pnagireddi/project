package com.abc.telecom.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DtoPackageTest {

    @Test
    void auth_request_and_response_roundtrip() {
        AuthRequest r = new AuthRequest();
        r.setUsername("bob");
        r.setPassword("p");

        assertThat(r.getUsername()).isEqualTo("bob");
        assertThat(r.getPassword()).isEqualTo("p");

        AuthResponse resp = new AuthResponse();
        resp.setToken("tkn");
        assertThat(resp.getToken()).isEqualTo("tkn");
    }

    @Test
    void invoice_and_payment_dtos() {
        InvoiceDto i = new InvoiceDto();
        i.setInvoiceId(7L);
        i.setCustomerId(3L);
        i.setTotalAmount(123.45);

        assertThat(i.getInvoiceId()).isEqualTo(7L);
        assertThat(i.getCustomerId()).isEqualTo(3L);
        assertThat(i.getTotalAmount()).isEqualTo(123.45);

        PaymentDto p = new PaymentDto();
        p.setInvoiceId(7L);
        p.setAmount(123.45);

        assertThat(p.getInvoiceId()).isEqualTo(7L);
        assertThat(p.getAmount()).isEqualTo(123.45);
    }

    @Test
    void usage_and_create_service_dto() {
        UsageDto u = new UsageDto();
        u.setServiceId(4L);
        u.setUsageAmount(10.0);
        assertThat(u.getServiceId()).isEqualTo(4L);
        assertThat(u.getUsageAmount()).isEqualTo(10.0);

        CreateServiceDto cs = new CreateServiceDto();
        cs.setServiceType("Mobile Plan");
        cs.setCustomerId(9L);
        assertThat(cs.getServiceType()).isEqualTo("Mobile Plan");
        assertThat(cs.getCustomerId()).isEqualTo(9L);
    }
}
