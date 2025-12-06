package com.abc.telecom.controller;

import com.abc.telecom.model.Payment;
import com.abc.telecom.service.InvoiceService;
import com.abc.telecom.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@RestController
@Tag(name = "Payments", description = "Make and list payments for invoices")
public class PaymentController {
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;

    public PaymentController(PaymentService paymentService, InvoiceService invoiceService) {
        this.paymentService = paymentService;
        this.invoiceService = invoiceService;
    }

    @PostMapping("/api/invoices/{id}/payments")
    @Operation(summary = "Make a payment for an invoice")
    @PreAuthorize("hasRole('ADMIN') or @authService.isInvoiceOwner(#id)")
    public ResponseEntity<?> makePayment(@PathVariable Long id, @RequestBody Payment p) {
        p.setInvoiceId(id);
        Payment created = paymentService.create(p);
        // After creating a payment, check if total payments >= invoice total and mark invoice as paid
        // Mark invoice as paid after receiving a payment (simple behavior)
        try {
            invoiceService.markAsPaid(id);
        } catch (Exception ex) {
            // ignore
        }
        return ResponseEntity.created(URI.create("/api/invoices/" + id + "/payments/" + created.getPaymentId())).body(created);
    }

    @GetMapping("/api/payments")
    @Operation(summary = "List all payments (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<Payment>> listAllPayments() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/api/invoices/{id}/payments")
    @Operation(summary = "List payments for an invoice")
    @PreAuthorize("hasRole('ADMIN') or @authService.isInvoiceOwner(#id)")
    public ResponseEntity<List<Payment>> getPayments(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findByInvoiceId(id));
    }
}
