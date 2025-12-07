package com.abc.telecom.service;

import com.abc.telecom.model.UsageRecord;
import com.abc.telecom.repository.UsageRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsageServiceTest {
    @Mock
    UsageRecordRepository repo;

    @InjectMocks
    UsageService svc;

    @Test
    void findByServiceId_delegates(){
        when(repo.findByServiceId(2L)).thenReturn(java.util.List.of(new UsageRecord()));
        var list = svc.findByServiceId(2L);
        assertThat(list).hasSize(1);
    }

    @Test
    void create_and_findById(){
        UsageRecord r = new UsageRecord(); r.setUsageAmount(3.3);
        when(repo.save(r)).thenReturn(r);
        when(repo.findById(9L)).thenReturn(Optional.of(r));
        assertThat(svc.create(r).getUsageAmount()).isEqualTo(3.3);
        assertThat(svc.findById(9L)).isPresent();
    }
}
