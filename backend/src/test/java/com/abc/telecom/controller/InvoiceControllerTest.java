package com.abc.telecom.controller;

import com.abc.telecom.model.Invoice;
import com.abc.telecom.model.InvoiceLine;
import com.abc.telecom.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InvoiceControllerTest {
    InvoiceService invoiceService;
    MockMvc mvc;
    

    @BeforeEach
    void setup() {
        invoiceService = Mockito.mock(InvoiceService.class);
        InvoiceController controller = new InvoiceController(invoiceService);
        mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getInvoices_returnsList() throws Exception {
        Invoice inv = new Invoice(); inv.setInvoiceId(9L);
        Mockito.when(invoiceService.findByCustomerId(3L)).thenReturn(List.of(inv));

        mvc.perform(get("/api/customers/3/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invoiceId").value(9));
    }

    @Test
    void createInvoice_withoutLines_callsGenerate() throws Exception {
        Invoice created = new Invoice(); created.setInvoiceId(77L);
        Mockito.when(invoiceService.generateInvoice(eq(5L), any(), any(), any())).thenReturn(created);

        String url = "/api/customers/5/invoices?start=" + LocalDate.now().toString() + "&end=" + LocalDate.now().toString();
        mvc.perform(post(url).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(77));
    }

    @Test
    void createInvoice_withLines_callsGenerateWithLines() throws Exception {
        Invoice created = new Invoice(); created.setInvoiceId(88L);
        Mockito.when(invoiceService.generateInvoice(eq(6L), any(), any(), any(), any())).thenReturn(created);

        String url = "/api/customers/6/invoices?start=" + LocalDate.now().toString() + "&end=" + LocalDate.now().toString();
        String body = "[ { \"description\": \"L1\", \"amount\": 12.5 } ]";

        mvc.perform(post(url).contentType("application/json").content(body)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(88));
    }

    @Test
    void getInvoice_returnsFoundOrNotFound() throws Exception {
        Invoice inv = new Invoice(); inv.setInvoiceId(200L);
        Mockito.when(invoiceService.findById(200L)).thenReturn(Optional.of(inv));
        Mockito.when(invoiceService.findById(201L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/invoices/200")).andExpect(status().isOk()).andExpect(jsonPath("$.invoiceId").value(200));
        mvc.perform(get("/api/invoices/201")).andExpect(status().isNotFound());
    }

    @Test
    void sendInvoice_marksSentOrNotFound() throws Exception {
        Invoice inv = new Invoice(); inv.setInvoiceId(300L);
        Mockito.when(invoiceService.markAsSent(300L)).thenReturn(Optional.of(inv));
        Mockito.when(invoiceService.markAsSent(301L)).thenReturn(Optional.empty());

        mvc.perform(post("/api/invoices/300/send")).andExpect(status().isOk()).andExpect(jsonPath("$.invoiceId").value(300));
        mvc.perform(post("/api/invoices/301/send")).andExpect(status().isNotFound());
    }

    @Test
    void getInvoiceLines_returnsList() throws Exception {
        InvoiceLine l = new InvoiceLine(); l.setId(11L); l.setDescription("line");
        Mockito.when(invoiceService.findLinesByInvoiceId(50L)).thenReturn(List.of(l));

        mvc.perform(get("/api/invoices/50/lines")).andExpect(status().isOk()).andExpect(jsonPath("$[0].description").value("line"));
    }
}
