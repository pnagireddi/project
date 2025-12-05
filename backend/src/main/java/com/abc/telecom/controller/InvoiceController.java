package com.abc.telecom.controller;

import com.abc.telecom.model.Invoice;
import com.abc.telecom.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) { this.invoiceService = invoiceService; }

    @GetMapping("/api/customers/{id}/invoices")
    public ResponseEntity<List<Invoice>> getInvoices(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findByCustomerId(id));
    }

    @PostMapping("/api/customers/{id}/invoices")
    public ResponseEntity<?> createInvoice(@PathVariable Long id, @RequestParam String start, @RequestParam String end) {
        LocalDateTime s = LocalDateTime.parse(start);
        LocalDateTime e = LocalDateTime.parse(end);
        LocalDateTime due = e.plusDays(15);
        Invoice created = invoiceService.generateInvoice(id, s, e, due);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/api/invoices/{id}")
    public ResponseEntity<?> getInvoice(@PathVariable Long id) {
        return invoiceService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
