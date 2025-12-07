package com.abc.telecom.config;

import com.abc.telecom.model.*;
import com.abc.telecom.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DataInitializerTest {

    @Test
    void seeds_when_missing() throws Exception {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        CustomerRepository customerRepo = Mockito.mock(CustomerRepository.class);
        TelecomServiceRepository svcRepo = Mockito.mock(TelecomServiceRepository.class);
        UsageRecordRepository usageRepo = Mockito.mock(UsageRecordRepository.class);
        InvoiceRepository invoiceRepo = Mockito.mock(InvoiceRepository.class);
        ServiceTemplateRepository tmplRepo = Mockito.mock(ServiceTemplateRepository.class);
        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);

        when(userRepo.existsByUsername("admin")).thenReturn(false);
        when(userRepo.existsByUsername("demo_customer")).thenReturn(false);
        when(tmplRepo.count()).thenReturn(0L);
        when(encoder.encode(any())).thenReturn("ENC");

        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setUserId(100L);
            return u;
        });
        when(customerRepo.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setCustomerId(200L);
            return c;
        });
        when(svcRepo.save(any(TelecomService.class))).thenAnswer(inv -> {
            TelecomService s = inv.getArgument(0);
            s.setServiceId(300L);
            return s;
        });
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        DataInitializer init = new DataInitializer(userRepo, customerRepo, svcRepo, usageRepo, invoiceRepo, tmplRepo, encoder);
        init.run();

        verify(userRepo, atLeastOnce()).save(any(User.class));
        verify(customerRepo, atLeastOnce()).save(any(Customer.class));
        verify(svcRepo, atLeastOnce()).save(any(TelecomService.class));
        verify(usageRepo, atLeastOnce()).save(any(UsageRecord.class));
        verify(invoiceRepo, atLeastOnce()).save(any(Invoice.class));
        verify(tmplRepo, atLeastOnce()).save(any(ServiceTemplate.class));
    }

    @Test
    void does_not_seed_when_present() throws Exception {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        CustomerRepository customerRepo = Mockito.mock(CustomerRepository.class);
        TelecomServiceRepository svcRepo = Mockito.mock(TelecomServiceRepository.class);
        UsageRecordRepository usageRepo = Mockito.mock(UsageRecordRepository.class);
        InvoiceRepository invoiceRepo = Mockito.mock(InvoiceRepository.class);
        ServiceTemplateRepository tmplRepo = Mockito.mock(ServiceTemplateRepository.class);
        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);

        when(userRepo.existsByUsername("admin")).thenReturn(true);
        when(userRepo.existsByUsername("demo_customer")).thenReturn(true);
        when(tmplRepo.count()).thenReturn(2L);

        DataInitializer init = new DataInitializer(userRepo, customerRepo, svcRepo, usageRepo, invoiceRepo, tmplRepo, encoder);
        init.run();

        verify(userRepo, never()).save(any(User.class));
        verify(customerRepo, never()).save(any(Customer.class));
        verify(svcRepo, never()).save(any(TelecomService.class));
        verify(usageRepo, never()).save(any(UsageRecord.class));
        verify(invoiceRepo, never()).save(any(Invoice.class));
        // templates count>0 so no seed saves
        verify(tmplRepo, never()).save(any(ServiceTemplate.class));
    }
}
