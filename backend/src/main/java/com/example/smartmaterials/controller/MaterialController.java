package com.example.smartmaterials.controller;

import com.example.smartmaterials.model.dto.MaterialRequest;
import com.example.smartmaterials.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public List<com.example.smartmaterials.entity.MaterialItem> list(@RequestParam(value = "q", required = false) String keyword) {
        return materialService.list(keyword);
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> create(@Valid @RequestBody MaterialRequest req) {
        materialService.create(req);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody MaterialRequest req) {
        materialService.update(id, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return ResponseEntity.ok().build();
    }
}
