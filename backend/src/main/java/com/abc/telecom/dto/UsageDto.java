package com.abc.telecom.dto;

import java.time.LocalDateTime;

public class UsageDto {
    private Long usageId;
    private Long serviceId;
    private LocalDateTime usageDate;
    private Double usageAmount;
    private String unit;

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
}
