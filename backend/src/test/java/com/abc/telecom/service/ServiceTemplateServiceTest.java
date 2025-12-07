package com.abc.telecom.service;

import com.abc.telecom.model.ServiceTemplate;
import com.abc.telecom.repository.ServiceTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTemplateServiceTest {
    @Mock
    ServiceTemplateRepository repo;

    @InjectMocks
    ServiceTemplateService svc;

    @Test
    void findAll_delegates_to_repo(){
        when(repo.findAll()).thenReturn(List.of(new ServiceTemplate()));
        var res = svc.findAll();
        assertThat(res).hasSize(1);
        verify(repo).findAll();
    }

    @Test
    void create_saves(){
        ServiceTemplate t = new ServiceTemplate(); t.setServiceName("T");
        when(repo.save(t)).thenReturn(t);
        var out = svc.create(t);
        assertThat(out.getServiceName()).isEqualTo("T");
    }
}
