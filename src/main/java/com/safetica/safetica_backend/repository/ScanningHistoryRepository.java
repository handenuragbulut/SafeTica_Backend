// ScanningHistoryRepository.java
package com.safetica.safetica_backend.repository;

import com.safetica.safetica_backend.entity.ScanningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScanningHistoryRepository extends JpaRepository<ScanningHistory, Long> {
    List<ScanningHistory> findAllByUserIdOrderByScannedAtDesc(Long userId);
}
