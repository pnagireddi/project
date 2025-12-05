package com.abc.telecom.controller;

import com.abc.telecom.model.UsageRecord;
import com.abc.telecom.service.UsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@RestController
@Tag(name = "Usage", description = "Usage records for services")
public class UsageController {
    private final UsageService usageService;

    public UsageController(UsageService usageService) { this.usageService = usageService; }

    @GetMapping("/api/services/{id}/usage")
    @Operation(summary = "Get usage records for a service")
    public ResponseEntity<List<UsageRecord>> getUsage(@PathVariable Long id) {
        return ResponseEntity.ok(usageService.findByServiceId(id));
    }

    @PostMapping("/api/services/{id}/usage")
    @Operation(summary = "Add a usage record for a service")
    public ResponseEntity<?> addUsage(@PathVariable Long id, @RequestBody UsageRecord u) {
        u.setServiceId(id);
        UsageRecord created = usageService.create(u);
        return ResponseEntity.created(URI.create("/api/services/" + id + "/usage/" + created.getUsageId())).body(created);
    }
}
