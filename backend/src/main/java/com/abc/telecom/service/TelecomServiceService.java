package com.abc.telecom.service;

import com.abc.telecom.model.TelecomService;
import com.abc.telecom.repository.TelecomServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TelecomServiceService {
    private final TelecomServiceRepository repo;

    public TelecomServiceService(TelecomServiceRepository repo) { this.repo = repo; }

    public Optional<TelecomService> findById(Long id) { return repo.findById(id); }

    public List<TelecomService> findByCustomerId(Long customerId) { return repo.findByCustomerId(customerId); }

    public TelecomService create(TelecomService s) { return repo.save(s); }

    public TelecomService update(TelecomService s) { return repo.save(s); }

    public void delete(Long id) { repo.deleteById(id); }
}
