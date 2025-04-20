// src/main/java/com/safetica/safetica_backend/service/ScanningHistoryService.java
package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.entity.ScanningHistory;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.repository.ScanningHistoryRepository;
import com.safetica.safetica_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScanningHistoryService {

    @Autowired private ScanningHistoryRepository scanRepo;
    @Autowired private UserRepository userRepo;

    /** Kullanıcının tarama geçmişini tarihine göre sıralı getir */
    public List<ScanningHistory> getHistory(Long userId) {
        return scanRepo.findAllByUserIdOrderByScannedAtDesc(userId);
    }

    /** Yeni bir tarama kaydı ekle */
    public ScanningHistory addScan(Long userId, String scannedData) {
        User u = userRepo.findById(userId)
                         .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ScanningHistory sh = new ScanningHistory();
        sh.setUser(u);
        sh.setScannedData(scannedData);
        sh.setScannedAt(LocalDateTime.now());
        return scanRepo.save(sh);
    }
}
