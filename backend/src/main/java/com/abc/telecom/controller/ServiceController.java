package com.abc.telecom.controller;

import com.abc.telecom.model.TelecomService;
import com.abc.telecom.service.TelecomServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final TelecomServiceService serviceService;

    public ServiceController(TelecomServiceService serviceService) { this.serviceService = serviceService; }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody TelecomService s) {
        return serviceService.findById(id).map(existing -> {
            s.setServiceId(id);
            return ResponseEntity.ok(serviceService.update(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
