package com.abc.telecom.config;

import com.abc.telecom.model.User;
import com.abc.telecom.model.Customer;
import com.abc.telecom.model.TelecomService;
import com.abc.telecom.model.UsageRecord;
import com.abc.telecom.model.Invoice;
import com.abc.telecom.repository.UserRepository;
import com.abc.telecom.repository.CustomerRepository;
import com.abc.telecom.repository.TelecomServiceRepository;
import com.abc.telecom.repository.UsageRecordRepository;
import com.abc.telecom.repository.InvoiceRepository;
import com.abc.telecom.repository.PaymentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final TelecomServiceRepository telecomServiceRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CustomerRepository customerRepository,
                           TelecomServiceRepository telecomServiceRepository,
                           UsageRecordRepository usageRecordRepository,
                           InvoiceRepository invoiceRepository,
                           PaymentRepository paymentRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.telecomServiceRepository = telecomServiceRepository;
        this.usageRecordRepository = usageRecordRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if missing
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setRole("ADMIN");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            userRepository.save(admin);
        }

        // Create demo customer user and profile
        if (!userRepository.existsByUsername("demo_customer")) {
            User demo = new User();
            demo.setUsername("demo_customer");
            demo.setEmail("customer@example.com");
            demo.setRole("CUSTOMER");
            demo.setPasswordHash(passwordEncoder.encode("customer123"));
            demo = userRepository.save(demo);

            Customer c = new Customer();
            c.setUserId(demo.getUserId());
            c.setFullName("Jane Demo");
            c.setAddress("123 Demo St");
            c.setPhoneNumber("+1-555-0100");
            c = customerRepository.save(c);

            // Add a sample service
            TelecomService svc = new TelecomService();
            svc.setCustomerId(c.getCustomerId());
            svc.setServiceName("Unlimited Mobile Plan");
            svc.setMonthlyFee(BigDecimal.valueOf(49.99));
            svc = telecomServiceRepository.save(svc);

            // Add a sample usage
            UsageRecord u = new UsageRecord();
            u.setServiceId(svc.getServiceId());
            u.setUsageDate(new Date());
            u.setUnits(1024L);
            u.setAmount(BigDecimal.valueOf(5.50));
            usageRecordRepository.save(u);

            // Create an invoice for last month
            Invoice inv = new Invoice();
            inv.setCustomerId(c.getCustomerId());
            inv.setBillingPeriodStart(Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            inv.setBillingPeriodEnd(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            inv.setTotalAmount(BigDecimal.valueOf(55.49));
            inv.setCreatedAt(new Date());
            inv.setDueDate(Date.from(LocalDate.now().plusWeeks(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            inv.setStatus("UNPAID");
            inv = invoiceRepository.save(inv);
        }
    }
}
