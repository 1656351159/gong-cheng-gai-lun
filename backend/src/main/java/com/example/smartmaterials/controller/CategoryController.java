package com.example.smartmaterials.controller;

import com.example.smartmaterials.model.dto.CategoryRequest;
import com.example.smartmaterials.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<com.example.smartmaterials.entity.MaterialCategory> list() {
        return categoryService.listAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> create(@Valid @RequestBody CategoryRequest req) {
        categoryService.create(req);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
        categoryService.update(id, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
