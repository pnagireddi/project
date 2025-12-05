package com.abc.telecom.controller;

import com.abc.telecom.model.Invoice;
import com.abc.telecom.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Tag(name = "Invoices", description = "Create and view invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) { this.invoiceService = invoiceService; }

    @GetMapping("/api/customers/{id}/invoices")
    @Operation(summary = "List invoices for a customer")
    public ResponseEntity<List<Invoice>> getInvoices(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findByCustomerId(id));
    }

    @PostMapping("/api/customers/{id}/invoices")
    @Operation(summary = "Generate an invoice for a customer over a period")
    public ResponseEntity<?> createInvoice(@PathVariable Long id, @RequestParam String start, @RequestParam String end) {
        LocalDateTime s = LocalDateTime.parse(start);
        LocalDateTime e = LocalDateTime.parse(end);
        LocalDateTime due = e.plusDays(15);
        Invoice created = invoiceService.generateInvoice(id, s, e, due);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/api/invoices/{id}")
    @Operation(summary = "Get an invoice by id")
    public ResponseEntity<?> getInvoice(@PathVariable Long id) {
        return invoiceService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
