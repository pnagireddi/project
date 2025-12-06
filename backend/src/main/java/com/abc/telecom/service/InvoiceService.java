package com.abc.telecom.service;

import com.abc.telecom.model.Invoice;
import com.abc.telecom.model.UsageRecord;
import com.abc.telecom.repository.InvoiceRepository;
import com.abc.telecom.repository.UsageRecordRepository;
import com.abc.telecom.repository.TelecomServiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final UsageRecordRepository usageRepo;
    private final TelecomServiceRepository serviceRepo;
    private final com.abc.telecom.repository.InvoiceLineRepository invoiceLineRepo;

    public InvoiceService(InvoiceRepository invoiceRepository, UsageRecordRepository usageRepo, TelecomServiceRepository serviceRepo, com.abc.telecom.repository.InvoiceLineRepository invoiceLineRepo) {
        this.invoiceRepository = invoiceRepository;
        this.usageRepo = usageRepo;
        this.serviceRepo = serviceRepo;
        this.invoiceLineRepo = invoiceLineRepo;
    }

    public List<Invoice> findByCustomerId(Long customerId) { return invoiceRepository.findByCustomerId(customerId); }

    public Optional<Invoice> findById(Long id) { return invoiceRepository.findById(id); }

    public java.util.List<com.abc.telecom.model.InvoiceLine> findLinesByInvoiceId(Long invoiceId) {
        return invoiceLineRepo.findByInvoiceId(invoiceId);
    }

    // Very simple invoice creation: sum usage amounts for customer's services within period
    public Invoice generateInvoice(Long customerId, LocalDateTime start, LocalDateTime end, LocalDateTime dueDate) {
        // legacy behavior: compute based on usage
        Invoice inv = new Invoice();
        inv.setCustomerId(customerId);
        inv.setBillingPeriodStart(start);
        inv.setBillingPeriodEnd(end);
        inv.setDueDate(dueDate);
        inv.setStatus("unpaid");

        double total = 0.0;
        var services = serviceRepo.findByCustomerId(customerId);
        for (var svc : services) {
            var usages = usageRepo.findByServiceId(svc.getServiceId());
            for (UsageRecord u : usages) {
                if ((u.getUsageDate().isEqual(start) || u.getUsageDate().isAfter(start)) && u.getUsageDate().isBefore(end.plusSeconds(1))) {
                    total += (u.getUsageAmount() == null ? 0.0 : u.getUsageAmount());
                }
            }
        }
        inv.setTotalAmount(total);
        return invoiceRepository.save(inv);
    }

    // overload that accepts explicit invoice lines (admin-provided)
    public Invoice generateInvoice(Long customerId, LocalDateTime start, LocalDateTime end, LocalDateTime dueDate, java.util.List<com.abc.telecom.model.InvoiceLine> lines) {
        Invoice inv = new Invoice();
        inv.setCustomerId(customerId);
        inv.setBillingPeriodStart(start);
        inv.setBillingPeriodEnd(end);
        inv.setDueDate(dueDate);
        inv.setStatus("unpaid");
        double total = 0.0;
        if (lines != null && !lines.isEmpty()) {
            for (var l : lines) {
                total += (l.getAmount() == null ? 0.0 : l.getAmount());
            }
        }
        inv.setTotalAmount(total);
        Invoice saved = invoiceRepository.save(inv);
        if (lines != null && !lines.isEmpty()) {
            for (var l : lines) {
                l.setInvoiceId(saved.getInvoiceId());
            }
            invoiceLineRepo.saveAll(lines);
        }
        return saved;
    }

    public Invoice save(Invoice invoice) { return invoiceRepository.save(invoice); }

    public Optional<Invoice> markAsSent(Long invoiceId) {
        var opt = invoiceRepository.findById(invoiceId);
        if (opt.isEmpty()) return Optional.empty();
        Invoice inv = opt.get();
        // set a status or record sent timestamp by updating status to 'sent' if unpaid
        if (inv.getStatus() == null || inv.getStatus().isEmpty()) inv.setStatus("unpaid");
        // we won't change paid/unpaid here, but mark as 'sent' by appending a sent flag in status
        if (!inv.getStatus().toLowerCase().contains("sent")) inv.setStatus(inv.getStatus() + "-sent");
        invoiceRepository.save(inv);
        return Optional.of(inv);
    }

    public Optional<Invoice> markAsPaid(Long invoiceId) {
        var opt = invoiceRepository.findById(invoiceId);
        if (opt.isEmpty()) return Optional.empty();
        Invoice inv = opt.get();
        inv.setStatus("paid");
        invoiceRepository.save(inv);
        return Optional.of(inv);
    }
}
