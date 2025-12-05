package com.abc.telecom.repository;

import com.abc.telecom.model.TelecomService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TelecomServiceRepository extends JpaRepository<TelecomService, Long> {
    List<TelecomService> findByCustomerId(Long customerId);
}
