package com.abc.telecom.controller;

import com.abc.telecom.model.ServiceTemplate;
import com.abc.telecom.service.ServiceTemplateService;
import com.abc.telecom.service.TelecomServiceService;
import com.abc.telecom.model.TelecomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/services/templates")
@Tag(name = "Service Templates", description = "Admin-managed service templates")
public class ServiceTemplateController {
    private final ServiceTemplateService templateService;
    private final TelecomServiceService telecomServiceService;

    public ServiceTemplateController(ServiceTemplateService templateService, TelecomServiceService telecomServiceService) {
        this.templateService = templateService;
        this.telecomServiceService = telecomServiceService;
    }

    @GetMapping("")
    @Operation(summary = "List available service templates")
    public ResponseEntity<List<ServiceTemplate>> list() {
        return ResponseEntity.ok(templateService.findAll());
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new service template (admin only)")
    public ResponseEntity<?> create(@RequestBody ServiceTemplate t) {
        var saved = templateService.create(t);
        return ResponseEntity.created(URI.create("/api/services/templates/" + saved.getId())).body(saved);
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign template to customer (admin)")
    public ResponseEntity<?> assignToCustomer(@PathVariable Long id, @RequestParam Long customerId, @RequestParam(required = false) Double monthlyFee) {
        var tplOpt = templateService.findById(id);
        if (tplOpt.isEmpty()) return ResponseEntity.notFound().build();
        var tpl = tplOpt.get();
        TelecomService svc = new TelecomService();
        svc.setCustomerId(customerId);
        svc.setServiceName(tpl.getServiceName());
        svc.setMonthlyFee(monthlyFee != null ? monthlyFee : tpl.getMonthlyFee());
        svc.setStatus("active");
        var created = telecomServiceService.create(svc);
        return ResponseEntity.ok(created);
    }
}
