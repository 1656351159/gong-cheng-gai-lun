package com.example.smartmaterials.controller;

import com.example.smartmaterials.model.dto.InboundRequest;
import com.example.smartmaterials.service.InboundService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inbound")
public class InboundController {

    private final InboundService inboundService;

    public InboundController(InboundService inboundService) {
        this.inboundService = inboundService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('admin','lab')")
    public ResponseEntity<Void> create(@Valid @RequestBody InboundRequest req) {
        inboundService.create(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin','lab')")
    public List<Map<String, Object>> list(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to) {
        return inboundService.list(from, to);
    }
}
