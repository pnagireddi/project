package com.abc.telecom.security;

import com.abc.telecom.model.Customer;
import com.abc.telecom.model.User;
import com.abc.telecom.model.Invoice;
import com.abc.telecom.repository.CustomerRepository;
import com.abc.telecom.repository.UserRepository;
import com.abc.telecom.repository.InvoiceRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("authService")
public class AuthService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;

    public AuthService(UserRepository userRepository, CustomerRepository customerRepository, InvoiceRepository invoiceRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.invoiceRepository = invoiceRepository;
    }

    private Optional<User> getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return Optional.empty();
        return userRepository.findByUsername(auth.getName());
    }

    public boolean isOwner(Long customerId) {
        var u = getCurrentUser();
        if (u.isEmpty()) return false;
        User user = u.get();
        if (user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN")) return true;
        List<Customer> cs = customerRepository.findByUserId(user.getUserId());
        for (Customer c : cs) {
            if (c.getCustomerId() != null && c.getCustomerId().equals(customerId)) return true;
        }
        return false;
    }

    public boolean isInvoiceOwner(Long invoiceId) {
        var u = getCurrentUser();
        if (u.isEmpty()) return false;
        User user = u.get();
        if (user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN")) return true;
        Optional<Invoice> inv = invoiceRepository.findById(invoiceId);
        if (inv.isEmpty()) return false;
        Invoice i = inv.get();
        Long custId = i.getCustomerId();
        if (custId == null) return false;
        List<Customer> cs = customerRepository.findByUserId(user.getUserId());
        for (Customer c : cs) {
            if (c.getCustomerId() != null && c.getCustomerId().equals(custId)) return true;
        }
        return false;
    }

    public java.util.Optional<Long> getCurrentCustomerId() {
        var u = getCurrentUser();
        if (u.isEmpty()) return java.util.Optional.empty();
        User user = u.get();
        if (user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN")) return java.util.Optional.empty();
        java.util.List<Customer> cs = customerRepository.findByUserId(user.getUserId());
        if (cs == null || cs.isEmpty()) return java.util.Optional.empty();
        return java.util.Optional.ofNullable(cs.get(0).getCustomerId());
    }
}
