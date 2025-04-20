// src/main/java/com/safetica/safetica_backend/controller/SavedArticleController.java
package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.entity.SavedArticle;
import com.safetica.safetica_backend.service.SavedArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-articles")
public class SavedArticleController {

    @Autowired
    private SavedArticleService savedArticleService;

    /** 
     * Kullanıcının kaydettiği tüm makaleleri getir 
     * GET /api/saved-articles?userId=123
     */
    @GetMapping
    public ResponseEntity<List<SavedArticle>> list(
            @RequestParam("userId") Long userId
    ) {
        List<SavedArticle> list = savedArticleService.listSavedArticles(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * Makaleyi kaydet
     * POST /api/saved-articles?userId=123&articleId=456
     */
    @PostMapping
    public ResponseEntity<SavedArticle> save(
            @RequestParam("userId") Long userId,
            @RequestParam("articleId") Long articleId
    ) {
        SavedArticle sa = savedArticleService.saveArticle(userId, articleId);
        return ResponseEntity.ok(sa);
    }

    /**
     * Kaydı kaldır
     * DELETE /api/saved-articles?userId=123&articleId=456
     */
    @DeleteMapping
    public ResponseEntity<Void> remove(
            @RequestParam("userId") Long userId,
            @RequestParam("articleId") Long articleId
    ) {
        savedArticleService.removeSavedArticle(userId, articleId);
        return ResponseEntity.noContent().build();
    }
}
