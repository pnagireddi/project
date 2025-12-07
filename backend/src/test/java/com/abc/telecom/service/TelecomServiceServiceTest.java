package com.abc.telecom.service;

import com.abc.telecom.model.TelecomService;
import com.abc.telecom.repository.TelecomServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelecomServiceServiceTest {
    @Mock
    TelecomServiceRepository repo;

    @InjectMocks
    TelecomServiceService svc;

    @Test
    void findById_delegates(){
        TelecomService s = new TelecomService(); s.setServiceId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(s));
        var out = svc.findById(1L);
        assertThat(out).isPresent();
    }

    @Test
    void findByCustomerId_returns_list(){
        when(repo.findByCustomerId(5L)).thenReturn(List.of(new TelecomService()));
        var list = svc.findByCustomerId(5L);
        assertThat(list).hasSize(1);
    }

    @Test
    void create_update_and_delete(){
        TelecomService s = new TelecomService(); s.setServiceName("X");
        when(repo.save(s)).thenReturn(s);
        assertThat(svc.create(s).getServiceName()).isEqualTo("X");
        assertThat(svc.update(s).getServiceName()).isEqualTo("X");
        doNothing().when(repo).deleteById(9L);
        svc.delete(9L);
        verify(repo).deleteById(9L);
    }
}
