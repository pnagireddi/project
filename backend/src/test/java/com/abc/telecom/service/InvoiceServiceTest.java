package com.abc.telecom.service;

import com.abc.telecom.model.Invoice;
import com.abc.telecom.model.InvoiceLine;
import com.abc.telecom.model.UsageRecord;
import com.abc.telecom.model.TelecomService;
import com.abc.telecom.repository.InvoiceRepository;
import com.abc.telecom.repository.UsageRecordRepository;
import com.abc.telecom.repository.TelecomServiceRepository;
import com.abc.telecom.repository.InvoiceLineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    @Mock
    InvoiceRepository invoiceRepository;

    @Mock
    UsageRecordRepository usageRepo;

    @Mock
    TelecomServiceRepository serviceRepo;

    @Mock
    InvoiceLineRepository invoiceLineRepo;

    @InjectMocks
    InvoiceService invoiceService;

    @Captor
    ArgumentCaptor<Invoice> invoiceCaptor;

    @BeforeEach
    void setup(){ }

    @Test
    void generateInvoice_sums_usage_for_customer(){
        // prepare services
        TelecomService svc = new TelecomService(); svc.setServiceId(1L); svc.setCustomerId(10L);
        when(serviceRepo.findByCustomerId(10L)).thenReturn(List.of(svc));

        UsageRecord u1 = new UsageRecord(); u1.setUsageAmount(10.0); u1.setUsageDate(LocalDateTime.now()); u1.setServiceId(1L);
        UsageRecord u2 = new UsageRecord(); u2.setUsageAmount(5.5); u2.setUsageDate(LocalDateTime.now()); u2.setServiceId(1L);
        when(usageRepo.findByServiceId(1L)).thenReturn(List.of(u1,u2));

        when(invoiceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime due = end.plusDays(15);
        Invoice inv = invoiceService.generateInvoice(10L, start, end, due);

        assertThat(inv).isNotNull();
        assertThat(inv.getCustomerId()).isEqualTo(10L);
        assertThat(inv.getTotalAmount()).isEqualTo(15.5);
        verify(invoiceRepository, times(1)).save(invoiceCaptor.capture());
        assertThat(invoiceCaptor.getValue().getTotalAmount()).isEqualTo(15.5);
    }

    @Test
    void generateInvoice_with_explicit_lines_persists_lines_and_total(){
        when(invoiceRepository.save(any())).thenAnswer(i -> { Invoice x = i.getArgument(0); x.setInvoiceId(55L); return x; });
        when(invoiceLineRepo.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        InvoiceLine l1 = new InvoiceLine(); l1.setDescription("A"); l1.setAmount(12.5);
        InvoiceLine l2 = new InvoiceLine(); l2.setDescription("B"); l2.setAmount(7.5);

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime due = end.plusDays(15);

        Invoice saved = invoiceService.generateInvoice(20L, start, end, due, List.of(l1,l2));

        assertThat(saved).isNotNull();
        assertThat(saved.getInvoiceId()).isEqualTo(55L);
        assertThat(saved.getTotalAmount()).isEqualTo(20.0);
        verify(invoiceLineRepo, times(1)).saveAll(any());
    }

    @Test
    void markAsSent_and_markAsPaid_update_status(){
        Invoice inv = new Invoice(); inv.setInvoiceId(100L); inv.setStatus("unpaid");
        when(invoiceRepository.findById(100L)).thenReturn(Optional.of(inv));

        var sent = invoiceService.markAsSent(100L);
        assertThat(sent).isPresent();
        assertThat(sent.get().getStatus()).contains("sent");

        var paid = invoiceService.markAsPaid(100L);
        assertThat(paid).isPresent();
        assertThat(paid.get().getStatus()).isEqualTo("paid");
    }
}
