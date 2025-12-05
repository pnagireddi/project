package com.abc.telecom.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    private Long customerId;

    private LocalDateTime billingPeriodStart;
    private LocalDateTime billingPeriodEnd;

    private Double totalAmount;

    private String status; // paid / unpaid

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime dueDate;

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public LocalDateTime getBillingPeriodStart() { return billingPeriodStart; }
    public void setBillingPeriodStart(LocalDateTime billingPeriodStart) { this.billingPeriodStart = billingPeriodStart; }
    public LocalDateTime getBillingPeriodEnd() { return billingPeriodEnd; }
    public void setBillingPeriodEnd(LocalDateTime billingPeriodEnd) { this.billingPeriodEnd = billingPeriodEnd; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
}
