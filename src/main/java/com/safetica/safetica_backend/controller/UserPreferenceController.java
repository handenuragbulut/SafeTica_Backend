package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.dto.UserPreferenceDTO;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.service.UserPreferenceService;
import com.safetica.safetica_backend.service.UserService;
import com.safetica.safetica_backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class UserPreferenceController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPreferenceService preferenceService;

    // ✅ Tercihleri kaydet
    @PostMapping("/save")
    public void savePreferences(@RequestBody UserPreferenceDTO dto, HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        String email = jwtUtil.getEmailFromToken(token);
        userService.findByEmail(email).ifPresent(user -> {
            preferenceService.savePreferences(user.getId(), dto);
        });
    }

    // // ✅ Tercihleri güncelle
    // @PutMapping("/update")
    // public void updatePreferences(@RequestBody UserPreferenceDTO dto, HttpServletRequest request) {
    //     String token = jwtUtil.extractTokenFromRequest(request);
    //     String email = jwtUtil.getEmailFromToken(token);
    //     userService.findByEmail(email).ifPresent(user -> {
    //         preferenceService.updatePreferences(user.getId(), dto);
    //     });
    // }

    // ✅ Mevcut tercihleri getir
    @GetMapping("/me")
    public UserPreferenceDTO getPreferences(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        String email = jwtUtil.getEmailFromToken(token);
        return userService.findByEmail(email)
                .map(user -> preferenceService.getPreferences(user.getId()))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
