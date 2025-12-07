package com.abc.telecom.service;

import com.abc.telecom.model.Payment;
import com.abc.telecom.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    PaymentRepository repo;

    @InjectMocks
    PaymentService svc;

    @Test
    void create_and_find(){
        Payment p = new Payment(); p.setAmount(50.0);
        when(repo.save(p)).thenReturn(p);
        when(repo.findByInvoiceId(2L)).thenReturn(List.of(p));
        when(repo.findAll()).thenReturn(List.of(p));
        assertThat(svc.create(p).getAmount()).isEqualTo(50.0);
        assertThat(svc.findByInvoiceId(2L)).hasSize(1);
        assertThat(svc.findAll()).hasSize(1);
    }
}
