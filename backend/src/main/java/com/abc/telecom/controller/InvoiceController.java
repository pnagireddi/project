package com.abc.telecom.controller;

import com.abc.telecom.model.Invoice;
import com.abc.telecom.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;

@RestController
@Tag(name = "Invoices", description = "Create and view invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) { this.invoiceService = invoiceService; }

    @GetMapping("/api/customers/{id}/invoices")
    @Operation(summary = "List invoices for a customer")
    @PreAuthorize("hasRole('ADMIN') or @authService.isOwner(#id)")
    public ResponseEntity<List<Invoice>> getInvoices(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findByCustomerId(id));
    }

    @PostMapping("/api/customers/{id}/invoices")
    @Operation(summary = "Generate an invoice for a customer over a period")
    @PreAuthorize("hasRole('ADMIN') or @authService.isOwner(#id)")
        public ResponseEntity<?> createInvoice(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestBody(required = false) java.util.List<com.abc.telecom.model.InvoiceLine> lines) {
        // Spring will parse yyyy-MM-dd into LocalDate; convert to LocalDateTime range
        LocalDate sd = start;
        LocalDate ed = end;
        LocalDateTime s = sd.atStartOfDay();
        LocalDateTime e = ed.atTime(23, 59, 59);
        LocalDateTime due = e.plusDays(15);
        Invoice created;
        if (lines != null && !lines.isEmpty()) {
            created = invoiceService.generateInvoice(id, s, e, due, lines);
        } else {
            created = invoiceService.generateInvoice(id, s, e, due);
        }
        return ResponseEntity.ok(created);
    }

    @GetMapping("/api/invoices/{id}")
    @Operation(summary = "Get an invoice by id")
    @PreAuthorize("hasRole('ADMIN') or @authService.isInvoiceOwner(#id)")
    public ResponseEntity<?> getInvoice(@PathVariable Long id) {
        return invoiceService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/invoices/{id}/send")
    @Operation(summary = "Mark invoice as sent (admin or owner)")
    @PreAuthorize("hasRole('ADMIN') or @authService.isInvoiceOwner(#id)")
    public ResponseEntity<?> sendInvoice(@PathVariable Long id) {
        return invoiceService.markAsSent(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/invoices/{id}/lines")
    @Operation(summary = "Get invoice lines")
    @PreAuthorize("hasRole('ADMIN') or @authService.isInvoiceOwner(#id)")
    public ResponseEntity<?> getInvoiceLines(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findLinesByInvoiceId(id));
    }
}
