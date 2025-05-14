package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.dto.SavedArticleDTO;
import com.safetica.safetica_backend.entity.SavedArticle;
import com.safetica.safetica_backend.service.SavedArticleService;
import com.safetica.safetica_backend.service.UserService;
import com.safetica.safetica_backend.util.JwtUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-articles")
public class SavedArticleController {

    private final SavedArticleService savedArticleService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public SavedArticleController(SavedArticleService savedArticleService, JwtUtil jwtUtil, UserService userService) {
        this.savedArticleService = savedArticleService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /** 🔄 Giriş yapan kullanıcının tüm kayıtlı makalelerini döner */
    @GetMapping
    public ResponseEntity<List<SavedArticleDTO>> getUserSavedArticles(
            @RequestHeader("Authorization") String tokenHeader) {

        String token = tokenHeader.replace("Bearer ", "").trim();
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.getEmailFromToken(token);
        Long userId = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        List<SavedArticleDTO> dtos = savedArticleService.getSavedArticleDTOsByUserId(userId);
        return ResponseEntity.ok(dtos);
    }

    /** ➕ Giriş yapan kullanıcı için blog yazısını kaydeder */
    @PostMapping("/{postId}")
    public ResponseEntity<SavedArticle> addSavedArticle(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String tokenHeader) {

        String token = tokenHeader.replace("Bearer ", "").trim();
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.getEmailFromToken(token);
        Long userId = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        SavedArticle article = savedArticleService.saveArticle(userId, postId);
        return ResponseEntity.ok(article);
    }

    /** ➖ Giriş yapan kullanıcı için blog yazısını kaldırır */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removeSavedArticle(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String tokenHeader) {

        String token = tokenHeader.replace("Bearer ", "").trim();
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.getEmailFromToken(token);
        Long userId = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        savedArticleService.unsaveArticle(userId, postId);
        return ResponseEntity.noContent().build();
    }

    /** ✅ Belirli bir yazı kaydedilmiş mi kontrol eder (opsiyonel kullanılabilir) */
    @GetMapping("/is-saved")
    public ResponseEntity<Boolean> isArticleSaved(
            @RequestParam Long postId,
            @RequestHeader("Authorization") String tokenHeader) {

        String token = tokenHeader.replace("Bearer ", "").trim();
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.getEmailFromToken(token);
        Long userId = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        boolean saved = savedArticleService.isArticleSaved(userId, postId);
        return ResponseEntity.ok(saved);
    }
}
