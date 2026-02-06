// src/main/java/com/boatsafari/controller/PDFExportController.java
package com.boatsafari.controller;

import com.boatsafari.service.PDFExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/operations")
@CrossOrigin(origins = "*")
public class PDFExportController {

    @Autowired
    private PDFExportService pdfExportService;

    @GetMapping("/export/revenue-pdf")
    public ResponseEntity<byte[]> exportRevenuePDF() {
        try {
            byte[] pdfBytes = pdfExportService.generateRevenueReport();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "revenue_report_" + timestamp + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/trip-performance-pdf")
    public ResponseEntity<byte[]> exportTripPerformancePDF() {
        try {
            byte[] pdfBytes = pdfExportService.generateTripPerformanceReport();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "trip_performance_report_" + timestamp + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}