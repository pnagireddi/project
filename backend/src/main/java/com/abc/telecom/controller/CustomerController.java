package com.abc.telecom.controller;

import com.abc.telecom.model.Customer;
import com.abc.telecom.model.TelecomService;
import com.abc.telecom.service.CustomerService;
import com.abc.telecom.service.TelecomServiceService;
import com.abc.telecom.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer profiles and services")
public class CustomerController {
    


    private final CustomerService customerService;
    private final TelecomServiceService serviceService;
    private final AuthService authService;
    private final com.abc.telecom.service.ServiceTemplateService templateService;

    public CustomerController(CustomerService customerService, TelecomServiceService serviceService, AuthService authService, com.abc.telecom.service.ServiceTemplateService templateService) {
        this.customerService = customerService;
        this.serviceService = serviceService;
        this.authService = authService;
        this.templateService = templateService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer profile by id")
    @PreAuthorize("hasRole('ADMIN') or @authService.isOwner(#id)")
    public ResponseEntity<?> getCustomer(@PathVariable Long id) {
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/services")
    @Operation(summary = "List services for a customer")
    @PreAuthorize("hasRole('ADMIN') or @authService.isOwner(#id)")
    public ResponseEntity<List<TelecomService>> getServices(@PathVariable Long id) {
        List<TelecomService> list = serviceService.findByCustomerId(id);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/services")
    @Operation(summary = "Create a telecom service for a customer (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createService(@PathVariable Long id, @RequestBody TelecomService svc) {
        svc.setCustomerId(id);
        TelecomService created = serviceService.create(svc);
        return ResponseEntity.created(URI.create("/api/services/" + created.getServiceId())).body(created);
    }

    @GetMapping("/me/services")
    @Operation(summary = "List services for current logged in customer")
    public ResponseEntity<List<TelecomService>> getMyServices() {
        var cidOpt = authService.getCurrentCustomerId();
        if (cidOpt.isEmpty()) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(serviceService.findByCustomerId(cidOpt.get()));
    }

    @PostMapping("/me/services/subscribe")
    @Operation(summary = "Subscribe current customer to a service template")
    public ResponseEntity<?> subscribeToTemplate(@RequestBody java.util.Map<String, Object> body) {
        var cidOpt = authService.getCurrentCustomerId();
        if (cidOpt.isEmpty()) return ResponseEntity.status(401).build();
        Object tidObj = body.get("templateId");
        if (tidObj == null) return ResponseEntity.badRequest().body("templateId required");
        Long tid = Long.valueOf(String.valueOf(tidObj));
        Double override = null;
        if (body.containsKey("monthlyFee")) {
            try { override = Double.valueOf(String.valueOf(body.get("monthlyFee"))); } catch(Exception e){}
        }
        // delegate to service-template service via ServiceTemplateService
        // lazy lookup using application context is avoided; instead inject ServiceTemplateService if available
        try {
            var templateOpt = this.templateService.findById(tid);
            if (templateOpt.isEmpty()) return ResponseEntity.notFound().build();
            var tpl = templateOpt.get();
            TelecomService svc = new TelecomService();
            svc.setCustomerId(cidOpt.get());
            svc.setServiceName(tpl.getServiceName());
            svc.setMonthlyFee(override != null ? override : tpl.getMonthlyFee());
            svc.setStatus("active");
            var created = serviceService.create(svc);
            return ResponseEntity.ok(created);
        } catch(Exception ex){
            return ResponseEntity.status(500).body(ex.getMessage());
        }
    }

    @PostMapping("")
    @Operation(summary = "Create or update a customer profile")
    @PreAuthorize("hasRole('ADMIN') or @authService.isOwner(#c.customerId) or #c.customerId == null")
    public ResponseEntity<?> createOrUpdateCustomer(@RequestBody Customer c) {
        Customer saved = customerService.save(c);
        return ResponseEntity.created(URI.create("/api/customers/" + saved.getCustomerId())).body(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    @Operation(summary = "List all customers (admin only)")
    public ResponseEntity<?> listCustomers() {
        return ResponseEntity.ok(customerService.findAll());
    }
}
