package com.abc.telecom.service;

import com.abc.telecom.model.ServiceTemplate;
import com.abc.telecom.repository.ServiceTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceTemplateService {
    private final ServiceTemplateRepository repo;

    public ServiceTemplateService(ServiceTemplateRepository repo) { this.repo = repo; }

    public List<ServiceTemplate> findAll() { return repo.findAll(); }

    public ServiceTemplate create(ServiceTemplate t) { return repo.save(t); }

    public java.util.Optional<ServiceTemplate> findById(Long id) { return repo.findById(id); }
}
