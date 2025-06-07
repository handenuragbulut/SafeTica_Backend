package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.dto.BrandRepresentativeRequest;
import com.safetica.safetica_backend.entity.BrandRepresentative;
import com.safetica.safetica_backend.entity.Product;
import com.safetica.safetica_backend.service.BrandRepresentativeService;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.repository.ProductRepository;
import com.safetica.safetica_backend.repository.UserRepository;
import com.safetica.safetica_backend.service.ActivityLogService;
import com.safetica.safetica_backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;

@RestController
@RequestMapping("/api/representatives")
public class BrandRepresentativeController {

    private final BrandRepresentativeService brandRepService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ActivityLogService activityLogService;

    public BrandRepresentativeController(BrandRepresentativeService brandRepService,
            ProductRepository productRepository,
            UserRepository userRepository,
            JwtUtil jwtUtil, ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
        this.brandRepService = brandRepService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // ✅ 1. Temsilci Başvurusu
    @PostMapping("/apply")
    public ResponseEntity<BrandRepresentative> apply(@RequestBody BrandRepresentativeRequest request) {
        BrandRepresentative representative = new BrandRepresentative();
        representative.setCompanyName(request.getCompanyName());
        representative.setBrandName(request.getBrandName());
        representative.setFirstName(request.getFirstName());
        representative.setLastName(request.getLastName());
        representative.setContactEmail(request.getContactEmail());
        representative.setCountry(request.getCountry());
        representative.setPhoneNumber(request.getPhoneNumber());
        representative.setStatus("PENDING");

        BrandRepresentative saved = brandRepService.apply(representative, request.getPassword());
        activityLogService.log("applied", "Representative application submitted: " + saved.getBrandName(), null);
        return ResponseEntity.ok(saved);
    }

    // ✅ 2. Statüye Göre Listeleme (örn: ?status=PENDING)
    @GetMapping
    public ResponseEntity<List<BrandRepresentative>> listByStatus(@RequestParam String status) {
        List<BrandRepresentative> reps = brandRepService.listByStatus(status);
        return ResponseEntity.ok(reps);
    }

    // ✅ 3. Onaylama
    @PatchMapping("/{id}/approve")
    public ResponseEntity<BrandRepresentative> approve(@PathVariable Long id) {
        return brandRepService.approve(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 4. Reddetme
    @PatchMapping("/{id}/reject")
    public ResponseEntity<BrandRepresentative> reject(@PathVariable Long id) {
        return brandRepService.reject(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 5. Tüm Temsilcileri Listeleme
    @GetMapping("/all")
    public ResponseEntity<List<BrandRepresentative>> getAllRepresentatives() {
        List<BrandRepresentative> all = brandRepService.getAll();
        return ResponseEntity.ok(all);
    }

    // ✅ 6. ID ile temsilci getirme
    @GetMapping("/{id}")
    public ResponseEntity<BrandRepresentative> getById(@PathVariable Long id) {
        return brandRepService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 7. Güncelleme
    @PutMapping("/{id}")
    public ResponseEntity<BrandRepresentative> update(@PathVariable Long id, @RequestBody BrandRepresentative updated) {
        return brandRepService.update(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 8. Temsilciyi Silme
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRepresentative(@PathVariable Long id) {
        try {
            brandRepService.deleteRepresentative(id);
            return ResponseEntity.ok("Representative deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete representative: " + e.getMessage());
        }
    }

    // ✅ Yeni: Temsilci Dashboard verilerini döner
    @GetMapping("/dashboard")
    public ResponseEntity<?> getRepresentativeDashboard(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.status(401).body("Token missing");
        }

        String email = jwtUtil.getEmailFromToken(token);
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid user");
        }

        User user = userOpt.get();

        // 🟢 Temsilcinin ürün isteklerini al
        List<Product> allProducts = productRepository.findBySubmittedByRepresentativeId(user.getId());

        long totalRequests = allProducts.size();
        long approved = allProducts.stream().filter(p -> "APPROVED".equals(p.getStatus())).count();
        long rejected = allProducts.stream().filter(p -> "REJECTED".equals(p.getStatus())).count();
        long pending = allProducts.stream().filter(p -> "PENDING".equals(p.getStatus())).count();

        // 🟡 Son eklenen 3 ürün
        List<Map<String, Object>> recentProducts = allProducts.stream()
                .sorted(Comparator.comparing(Product::getCreatedAt).reversed())
                .limit(3)
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("name", p.getName());
                    map.put("imageUrl", p.getImageUrl());
                    return map;
                })
                .collect(Collectors.toList());

        // 🟣 Basit aktivite akışı (son 5 hareket)
        List<Map<String, Object>> activities = new ArrayList<>();
        allProducts.stream()
                .sorted(Comparator.comparing(Product::getUpdatedAt).reversed())
                .limit(5)
                .forEach(p -> activities.add(Map.of(
                        "type", p.getStatus().equals("PENDING") ? "added" : p.getStatus().toLowerCase(),
                        "message", p.getStatus().equals("PENDING")
                                ? "Product added: " + p.getName()
                                : "Admin " + p.getStatus().toLowerCase() + ": " + p.getName(),
                        "timestamp", p.getUpdatedAt())));

        // 🟧 JSON yanıtı
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("totalRequests", totalRequests);
        dashboardData.put("approved", approved);
        dashboardData.put("rejected", rejected);
        dashboardData.put("pending", pending);
        dashboardData.put("recentProducts", recentProducts);
        dashboardData.put("activities", activities);

        return ResponseEntity.ok(dashboardData);
    }
}
