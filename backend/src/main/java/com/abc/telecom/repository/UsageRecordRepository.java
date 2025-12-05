package com.abc.telecom.repository;

import com.abc.telecom.model.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {
    List<UsageRecord> findByServiceId(Long serviceId);
}
