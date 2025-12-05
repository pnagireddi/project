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

    public InvoiceService(InvoiceRepository invoiceRepository, UsageRecordRepository usageRepo, TelecomServiceRepository serviceRepo) {
        this.invoiceRepository = invoiceRepository;
        this.usageRepo = usageRepo;
        this.serviceRepo = serviceRepo;
    }

    public List<Invoice> findByCustomerId(Long customerId) { return invoiceRepository.findByCustomerId(customerId); }

    public Optional<Invoice> findById(Long id) { return invoiceRepository.findById(id); }

    // Very simple invoice creation: sum usage amounts for customer's services within period
    public Invoice generateInvoice(Long customerId, LocalDateTime start, LocalDateTime end, LocalDateTime dueDate) {
        Invoice inv = new Invoice();
        inv.setCustomerId(customerId);
        inv.setBillingPeriodStart(start);
        inv.setBillingPeriodEnd(end);
        inv.setDueDate(dueDate);
        inv.setStatus("unpaid");

        // Sum usage for services belonging to customer
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

    public Invoice save(Invoice invoice) { return invoiceRepository.save(invoice); }
}
