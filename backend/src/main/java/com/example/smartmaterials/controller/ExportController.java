package com.example.smartmaterials.controller;

import com.example.smartmaterials.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<byte[]> exportRequests(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to) {
        String csv = exportService.exportRequests(from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=requests.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv.getBytes());
    }

    @GetMapping("/stock")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<byte[]> exportStock() {
        String csv = exportService.exportStock();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv.getBytes());
    }
}
