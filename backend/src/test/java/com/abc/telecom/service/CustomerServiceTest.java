package com.abc.telecom.service;

import com.abc.telecom.model.Customer;
import com.abc.telecom.repository.CustomerRepository;
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
public class CustomerServiceTest {
    @Mock
    CustomerRepository repo;

    @InjectMocks
    CustomerService svc;

    @Test
    void findById_and_save_and_delete(){
        Customer c = new Customer(); c.setCustomerId(5L);
        when(repo.findById(5L)).thenReturn(Optional.of(c));
        when(repo.save(c)).thenReturn(c);
        assertThat(svc.findById(5L)).isPresent();
        assertThat(svc.save(c).getCustomerId()).isEqualTo(5L);
        doNothing().when(repo).deleteById(5L);
        svc.delete(5L);
        verify(repo).deleteById(5L);
    }

    @Test
    void findByUserId_and_findAll(){
        Customer c = new Customer(); c.setCustomerId(7L); c.setUserId(10L);
        when(repo.findByUserId(10L)).thenReturn(List.of(c));
        when(repo.findAll()).thenReturn(List.of(c));
        assertThat(svc.findByUserId(10L)).isPresent();
        assertThat(svc.findAll()).hasSize(1);
    }
}
