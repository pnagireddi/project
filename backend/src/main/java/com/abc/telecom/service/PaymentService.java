package com.abc.telecom.service;

import com.abc.telecom.model.Payment;
import com.abc.telecom.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) { this.repo = repo; }

    public Payment create(Payment p) { return repo.save(p); }

    public List<Payment> findByInvoiceId(Long invoiceId) { return repo.findByInvoiceId(invoiceId); }
    public List<Payment> findAll() { return repo.findAll(); }
}
