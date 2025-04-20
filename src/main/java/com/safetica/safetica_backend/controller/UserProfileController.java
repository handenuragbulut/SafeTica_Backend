// src/main/java/com/safetica/safetica_backend/controller/UserProfileController.java
package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.entity.UserProfile;
import com.safetica.safetica_backend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Kullanıcının profili varsa 200 + gövde, yoksa 404 döner
     * GET /api/profiles/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> getProfile(@PathVariable Long userId) {
        return userProfileService
                .getByUserId(userId)             // Optional<UserProfile>
                .map(ResponseEntity::ok)         // varsa 200 + gövde
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Profil oluşturur veya günceller.
     * POST /api/profiles/{userId}
     * body = {
     *   "skinType": "...",
     *   "allergies": "...",
     *   "surveyDone": true
     * }
     */
    @PostMapping("/{userId}")
    public ResponseEntity<UserProfile> saveOrUpdateProfile(
            @PathVariable Long userId,
            @RequestBody UserProfile payload
    ) {
        UserProfile saved = userProfileService.saveOrUpdate(userId, payload);
        return ResponseEntity.ok(saved);
    }

    /**
     * (Opsiyonel) Profil silme
     * DELETE /api/profiles/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) {
        userProfileService.deleteByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
