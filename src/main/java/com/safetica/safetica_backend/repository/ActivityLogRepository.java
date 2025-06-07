package com.safetica.safetica_backend.repository;

import com.safetica.safetica_backend.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Son 10 aktiviteyi getir
    List<ActivityLog> findTop10ByOrderByTimestampDesc();

    // Eğer spesifik bir kullanıcı için getirilecekse
    List<ActivityLog> findByUserIdOrderByTimestampDesc(Long userId);
}
