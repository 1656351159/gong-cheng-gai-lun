package com.example.smartmaterials.controller;

import com.example.smartmaterials.service.StockService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin','lab')")
    public List<Map<String, Object>> list(@RequestParam(value = "q", required = false) String keyword) {
        return stockService.list(keyword);
    }
}
