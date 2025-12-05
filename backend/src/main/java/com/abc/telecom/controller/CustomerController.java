package com.abc.telecom.controller;

import com.abc.telecom.model.Customer;
import com.abc.telecom.model.TelecomService;
import com.abc.telecom.service.CustomerService;
import com.abc.telecom.service.TelecomServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final TelecomServiceService serviceService;

    public CustomerController(CustomerService customerService, TelecomServiceService serviceService) {
        this.customerService = customerService;
        this.serviceService = serviceService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable Long id) {
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<List<TelecomService>> getServices(@PathVariable Long id) {
        List<TelecomService> list = serviceService.findByCustomerId(id);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/services")
    public ResponseEntity<?> createService(@PathVariable Long id, @RequestBody TelecomService svc) {
        svc.setCustomerId(id);
        TelecomService created = serviceService.create(svc);
        return ResponseEntity.created(URI.create("/api/services/" + created.getServiceId())).body(created);
    }
}
