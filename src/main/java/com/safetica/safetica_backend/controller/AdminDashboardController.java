package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.entity.ActivityLog;
import com.safetica.safetica_backend.repository.ActivityLogRepository;
import com.safetica.safetica_backend.repository.BlogPostRepository;
import com.safetica.safetica_backend.repository.ProductRepository;
import com.safetica.safetica_backend.repository.UserRepository;
import com.safetica.safetica_backend.repository.BrandRepresentativeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BlogPostRepository blogPostRepository;
    private final BrandRepresentativeRepository brandRepRepository;
    private final ActivityLogRepository activityLogRepository;

    public AdminDashboardController(UserRepository userRepository,
            ProductRepository productRepository,
            BlogPostRepository blogPostRepository,
            BrandRepresentativeRepository brandRepRepository, ActivityLogRepository activityLogRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.blogPostRepository = blogPostRepository;
        this.brandRepRepository = brandRepRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalRepresentatives", brandRepRepository.count());
        stats.put("pendingRepresentatives", brandRepRepository.countByStatus("PENDING"));
        stats.put("pendingProducts", productRepository.countByStatus("PENDING"));
        stats.put("totalBlogs", blogPostRepository.count());

        // 🔥 Gerçek aktiviteleri getir
        List<ActivityLog> recentActivities = activityLogRepository.findTop10ByOrderByTimestampDesc();
        stats.put("recentActivities", recentActivities);

        return ResponseEntity.ok(stats);
    }

}
