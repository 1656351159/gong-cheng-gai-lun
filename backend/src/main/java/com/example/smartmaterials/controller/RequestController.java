package com.example.smartmaterials.controller;

import com.example.smartmaterials.model.dto.RequestApprove;
import com.example.smartmaterials.model.dto.RequestCreate;
import com.example.smartmaterials.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @PreAuthorize("hasRole('student')")
    public ResponseEntity<Void> create(@Valid @RequestBody RequestCreate req, Authentication auth) {
        requestService.create(req, auth.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('student')")
    public List<Map<String, Object>> my(Authentication auth) {
        return requestService.listByUser(auth.getName());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin','lab')")
    public List<Map<String, Object>> list() {
        return requestService.listAll();
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('admin','lab')")
    public ResponseEntity<Void> approve(@PathVariable Long id, @Valid @RequestBody RequestApprove req, Authentication auth) {
        requestService.approve(id, req, auth.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('admin','lab')")
    public ResponseEntity<Void> reject(@PathVariable Long id, @Valid @RequestBody RequestApprove req, Authentication auth) {
        requestService.reject(id, req, auth.getName());
        return ResponseEntity.ok().build();
    }
}
