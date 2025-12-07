package com.abc.telecom.security;

import com.abc.telecom.model.User;
import com.abc.telecom.model.Customer;
import com.abc.telecom.model.Invoice;
import com.abc.telecom.repository.CustomerRepository;
import com.abc.telecom.repository.UserRepository;
import com.abc.telecom.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    UserRepository userRepo;
    @Mock
    CustomerRepository customerRepo;
    @Mock
    InvoiceRepository invoiceRepo;

    @InjectMocks
    AuthService authService;

    @BeforeEach
    void init(){
        SecurityContext ctx = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void isOwner_returns_true_for_admin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        when(auth.getName()).thenReturn("admin");
        User u = new User(); u.setUserId(1L); u.setRole("ADMIN");
        when(userRepo.findByUsername("admin")).thenReturn(Optional.of(u));
        assertThat(authService.isOwner(5L)).isTrue();
    }

    @Test
    void isOwner_checks_customer_list(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        when(auth.getName()).thenReturn("bob");
        User u = new User(); u.setUserId(10L); u.setRole("CUSTOMER");
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(u));
        Customer c = new Customer(); c.setCustomerId(99L); c.setUserId(10L);
        when(customerRepo.findByUserId(10L)).thenReturn(List.of(c));
        assertThat(authService.isOwner(99L)).isTrue();
        assertThat(authService.isOwner(5L)).isFalse();
    }

    @Test
    void isInvoiceOwner_checks_owner_chain(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        when(auth.getName()).thenReturn("bob");
        User u = new User(); u.setUserId(10L); u.setRole("CUSTOMER");
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(u));
        Customer c = new Customer(); c.setCustomerId(99L); c.setUserId(10L);
        when(customerRepo.findByUserId(10L)).thenReturn(List.of(c));
        Invoice i = new Invoice(); i.setInvoiceId(7L); i.setCustomerId(99L);
        when(invoiceRepo.findById(7L)).thenReturn(Optional.of(i));
        assertThat(authService.isInvoiceOwner(7L)).isTrue();
    }
}
