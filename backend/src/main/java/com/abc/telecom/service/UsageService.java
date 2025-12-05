package com.abc.telecom.service;

import com.abc.telecom.model.UsageRecord;
import com.abc.telecom.repository.UsageRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsageService {
    private final UsageRecordRepository repo;

    public UsageService(UsageRecordRepository repo) { this.repo = repo; }

    public List<UsageRecord> findByServiceId(Long serviceId) { return repo.findByServiceId(serviceId); }

    public UsageRecord create(UsageRecord r) { return repo.save(r); }

    public Optional<UsageRecord> findById(Long id) { return repo.findById(id); }
}
