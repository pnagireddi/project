package com.abc.telecom.controller;

import com.abc.telecom.model.Payment;
import com.abc.telecom.service.InvoiceService;
import com.abc.telecom.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<?> makePayment(@PathVariable Long id, @RequestBody Payment p) {
        p.setInvoiceId(id);
        Payment created = paymentService.create(p);
        // Optionally mark invoice as paid if total covered (omitted)
        return ResponseEntity.created(URI.create("/api/invoices/" + id + "/payments/" + created.getPaymentId())).body(created);
    }

    @GetMapping("/api/invoices/{id}/payments")
    @Operation(summary = "List payments for an invoice")
    public ResponseEntity<List<Payment>> getPayments(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findByInvoiceId(id));
    }
}
