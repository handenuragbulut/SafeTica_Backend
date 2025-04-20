// src/main/java/com/safetica/safetica_backend/controller/ScanningHistoryController.java
package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.entity.ScanningHistory;
import com.safetica.safetica_backend.service.ScanningHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scans")
public class ScanningHistoryController {

    @Autowired
    private ScanningHistoryService scanningHistoryService;

    /** Kullanıcının tarama geçmişini tarih sırasıyla döner */
    @GetMapping("/{userId}")
    public ResponseEntity<List<ScanningHistory>> getHistory(
            @PathVariable Long userId
    ) {
        List<ScanningHistory> hist = scanningHistoryService.getHistory(userId);
        return ResponseEntity.ok(hist);
    }

    /** Yeni bir tarama kaydı ekler */
    @PostMapping
    public ResponseEntity<ScanningHistory> addScan(
            @RequestParam Long userId,
            @RequestParam String scannedData
    ) {
        ScanningHistory sh = scanningHistoryService.addScan(userId, scannedData);
        return ResponseEntity.ok(sh);
    }
}
