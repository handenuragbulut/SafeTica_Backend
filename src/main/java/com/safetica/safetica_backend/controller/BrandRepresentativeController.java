package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.dto.BrandRepresentativeRequest;
import com.safetica.safetica_backend.entity.BrandRepresentative;
import com.safetica.safetica_backend.service.BrandRepresentativeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/representatives")
public class BrandRepresentativeController {

    private final BrandRepresentativeService brandRepService;

    public BrandRepresentativeController(BrandRepresentativeService brandRepService) {
        this.brandRepService = brandRepService;
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
}
