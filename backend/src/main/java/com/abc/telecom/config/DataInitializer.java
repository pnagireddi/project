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
import com.abc.telecom.repository.ServiceTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

// cleaned up unused imports

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final TelecomServiceRepository telecomServiceRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final InvoiceRepository invoiceRepository;
    private final ServiceTemplateRepository serviceTemplateRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CustomerRepository customerRepository,
                           TelecomServiceRepository telecomServiceRepository,
                           UsageRecordRepository usageRecordRepository,
                           InvoiceRepository invoiceRepository,
                           ServiceTemplateRepository serviceTemplateRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.telecomServiceRepository = telecomServiceRepository;
        this.usageRecordRepository = usageRecordRepository;
        this.invoiceRepository = invoiceRepository;
        this.serviceTemplateRepository = serviceTemplateRepository;
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

            // Add a sample service (use new serviceName/monthlyFee fields)
            TelecomService svc = new TelecomService();
            svc.setCustomerId(c.getCustomerId());
            svc.setServiceName("Unlimited Mobile Plan");
            svc.setMonthlyFee(29.99);
            svc.setStartDate(java.time.LocalDateTime.now().minusMonths(1));
            svc.setStatus("active");
            svc = telecomServiceRepository.save(svc);

            // Add a sample usage
            UsageRecord u = new UsageRecord();
            u.setServiceId(svc.getServiceId());
            u.setUsageDate(java.time.LocalDateTime.now());
            u.setUnit("MB");
            u.setUsageAmount(5.5);
            usageRecordRepository.save(u);

            // Create an invoice for last month
            Invoice inv = new Invoice();
            inv.setCustomerId(c.getCustomerId());
            inv.setBillingPeriodStart(java.time.LocalDateTime.now().minusMonths(1));
            inv.setBillingPeriodEnd(java.time.LocalDateTime.now());
            inv.setTotalAmount(55.49);
            inv.setDueDate(java.time.LocalDateTime.now().plusWeeks(2));
            inv.setStatus("UNPAID");
            inv = invoiceRepository.save(inv);
        }

        // Seed some service templates if none exist
        try{
            if (serviceTemplateRepository.count() == 0) {
                var t1 = new com.abc.telecom.model.ServiceTemplate();
                t1.setServiceName("UNLIMITED");
                t1.setMonthlyFee(350.0);
                serviceTemplateRepository.save(t1);

                var t2 = new com.abc.telecom.model.ServiceTemplate();
                t2.setServiceName("DATA_2GB");
                t2.setMonthlyFee(20.0);
                serviceTemplateRepository.save(t2);
            }
        }catch(Exception ex){ /* ignore seeding failures */ }
    }
}
