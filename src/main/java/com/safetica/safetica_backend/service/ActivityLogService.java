package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.entity.ActivityLog;
import com.safetica.safetica_backend.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void log(String type, String message, Long userId) {
        ActivityLog activity = new ActivityLog();
        activity.setType(type);
        activity.setMessage(message);
        activity.setTimestamp(LocalDateTime.now());
        activity.setUserId(userId);

        activityLogRepository.save(activity);
    }
}
