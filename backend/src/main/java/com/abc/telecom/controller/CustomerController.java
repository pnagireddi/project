package com.abc.telecom.controller;

import com.abc.telecom.model.Customer;
import com.abc.telecom.model.TelecomService;
import com.abc.telecom.service.CustomerService;
import com.abc.telecom.service.TelecomServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    public CustomerController(CustomerService customerService, TelecomServiceService serviceService) {
        this.customerService = customerService;
        this.serviceService = serviceService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer profile by id")
    public ResponseEntity<?> getCustomer(@PathVariable Long id) {
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/services")
    @Operation(summary = "List services for a customer")
    public ResponseEntity<List<TelecomService>> getServices(@PathVariable Long id) {
        List<TelecomService> list = serviceService.findByCustomerId(id);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/services")
    @Operation(summary = "Create a telecom service for a customer")
    public ResponseEntity<?> createService(@PathVariable Long id, @RequestBody TelecomService svc) {
        svc.setCustomerId(id);
        TelecomService created = serviceService.create(svc);
        return ResponseEntity.created(URI.create("/api/services/" + created.getServiceId())).body(created);
    }

    @PostMapping("")
    @Operation(summary = "Create or update a customer profile")
    public ResponseEntity<?> createOrUpdateCustomer(@RequestBody Customer c) {
        Customer saved = customerService.save(c);
        return ResponseEntity.created(URI.create("/api/customers/" + saved.getCustomerId())).body(saved);
    }
}
