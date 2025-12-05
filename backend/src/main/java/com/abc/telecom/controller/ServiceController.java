package com.abc.telecom.controller;

import com.abc.telecom.model.TelecomService;
import com.abc.telecom.service.TelecomServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Services", description = "Manage telecom services")
public class ServiceController {
    private final TelecomServiceService serviceService;

    public ServiceController(TelecomServiceService serviceService) { this.serviceService = serviceService; }

    @PutMapping("/{id}")
    @Operation(summary = "Update a service by id")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody TelecomService s) {
        return serviceService.findById(id).map(existing -> {
            s.setServiceId(id);
            return ResponseEntity.ok(serviceService.update(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a service by id")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
