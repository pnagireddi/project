package com.abc.telecom.controller;

import com.abc.telecom.model.Payment;
import com.abc.telecom.service.InvoiceService;
import com.abc.telecom.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentControllerTest {
    PaymentService paymentService;
    InvoiceService invoiceService;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        paymentService = Mockito.mock(PaymentService.class);
        invoiceService = Mockito.mock(InvoiceService.class);
        PaymentController controller = new PaymentController(paymentService, invoiceService);
        mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void makePayment_createsPayment_andMarksInvoicePaid() throws Exception {
        Payment p = new Payment(); p.setPaymentId(12L); p.setAmount(50.0);
        when(paymentService.create(any())).thenReturn(p);

        String body = "{ \"amount\": 50.0, \"paymentMethod\": \"card\" }";
        mvc.perform(post("/api/invoices/5/payments").contentType("application/json").content(body)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/invoices/5/payments/12")))
                .andExpect(jsonPath("$.paymentId").value(12));

        verify(invoiceService).markAsPaid(5L);
    }

    @Test
    void makePayment_whenMarkPaidThrows_stillReturnsCreated() throws Exception {
        Payment p = new Payment(); p.setPaymentId(13L); p.setAmount(30.0);
        when(paymentService.create(any())).thenReturn(p);
        when(invoiceService.markAsPaid(6L)).thenThrow(new RuntimeException("boom"));

        String body = "{ \"amount\": 30.0 }";
        mvc.perform(post("/api/invoices/6/payments").contentType("application/json").content(body)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/invoices/6/payments/13")))
                .andExpect(jsonPath("$.paymentId").value(13));

        verify(invoiceService).markAsPaid(6L);
    }

    @Test
    void listAllPayments_returnsList() throws Exception {
        Payment p = new Payment(); p.setPaymentId(21L); p.setAmount(10.0);
        when(paymentService.findAll()).thenReturn(List.of(p));

        mvc.perform(get("/api/payments")).andExpect(status().isOk()).andExpect(jsonPath("$[0].paymentId").value(21));
    }

    @Test
    void getPayments_forInvoice_returnsList() throws Exception {
        Payment p = new Payment(); p.setPaymentId(31L); p.setInvoiceId(40L); p.setAmount(5.0);
        when(paymentService.findByInvoiceId(40L)).thenReturn(List.of(p));

        mvc.perform(get("/api/invoices/40/payments")).andExpect(status().isOk()).andExpect(jsonPath("$[0].paymentId").value(31));
    }
}
