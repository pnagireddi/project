package com.abc.telecom.model;

import jakarta.persistence.*;

@Entity
@Table(name = "service_templates")
public class ServiceTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;

    private Double monthlyFee;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public Double getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(Double monthlyFee) { this.monthlyFee = monthlyFee; }
}
