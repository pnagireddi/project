package com.abc.telecom.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelPojoTest {

    @Test
    void user_pojo() {
        User u = new User();
        u.setUserId(1L);
        u.setUsername("u1");
        u.setPasswordHash("ph");
        u.setEmail("a@b");
        u.setRole("CUSTOMER");
        assertThat(u.getUserId()).isEqualTo(1L);
        assertThat(u.getUsername()).isEqualTo("u1");
        assertThat(u.getPasswordHash()).isEqualTo("ph");
        assertThat(u.getEmail()).isEqualTo("a@b");
        assertThat(u.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    void customer_pojo() {
        Customer c = new Customer();
        c.setCustomerId(2L);
        c.setUserId(1L);
        c.setFullName("Name");
        c.setAddress("Addr");
        c.setPhoneNumber("P");
        assertThat(c.getCustomerId()).isEqualTo(2L);
        assertThat(c.getUserId()).isEqualTo(1L);
        assertThat(c.getFullName()).isEqualTo("Name");
    }

    @Test
    void telecom_service_pojo() {
        TelecomService s = new TelecomService();
        s.setServiceId(3L);
        s.setCustomerId(2L);
        s.setServiceName("S");
        s.setMonthlyFee(9.99);
        s.setStartDate(LocalDateTime.now());
        s.setStatus("active");
        assertThat(s.getServiceId()).isEqualTo(3L);
        assertThat(s.getMonthlyFee()).isEqualTo(9.99);
    }

    @Test
    void invoice_and_line_and_payment_and_usage_templates() {
        Invoice inv = new Invoice();
        inv.setInvoiceId(4L);
        inv.setCustomerId(2L);
        inv.setTotalAmount(50.0);
        inv.setStatus("UNPAID");
        inv.setDueDate(LocalDateTime.now());
        assertThat(inv.getInvoiceId()).isEqualTo(4L);

        InvoiceLine il = new InvoiceLine();
        il.setId(5L);
        il.setInvoiceId(4L);
        il.setDescription("desc");
        il.setAmount(20.0);
        assertThat(il.getDescription()).isEqualTo("desc");

        Payment p = new Payment();
        p.setPaymentId(6L);
        p.setInvoiceId(4L);
        p.setAmount(50.0);
        p.setPaymentMethod("card");
        assertThat(p.getAmount()).isEqualTo(50.0);

        UsageRecord ur = new UsageRecord();
        ur.setUsageId(7L);
        ur.setServiceId(3L);
        ur.setUsageAmount(12.5);
        ur.setUnit("MB");
        assertThat(ur.getUsageAmount()).isEqualTo(12.5);

        ServiceTemplate st = new ServiceTemplate();
        st.setId(8L);
        st.setServiceName("T");
        st.setMonthlyFee(30.0);
        assertThat(st.getMonthlyFee()).isEqualTo(30.0);
    }
}
