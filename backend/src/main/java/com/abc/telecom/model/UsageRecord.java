package com.abc.telecom.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "usage_records")
public class UsageRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usageId;

    private Long serviceId; // FK

    private LocalDateTime usageDate;

    private Double usageAmount;

    private String unit;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Long getUsageId() { return usageId; }
    public void setUsageId(Long usageId) { this.usageId = usageId; }
    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public LocalDateTime getUsageDate() { return usageDate; }
    public void setUsageDate(LocalDateTime usageDate) { this.usageDate = usageDate; }
    public Double getUsageAmount() { return usageAmount; }
    public void setUsageAmount(Double usageAmount) { this.usageAmount = usageAmount; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
