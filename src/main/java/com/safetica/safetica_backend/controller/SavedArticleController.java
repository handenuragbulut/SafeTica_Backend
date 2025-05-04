package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.dto.SavedArticleDTO;
import com.safetica.safetica_backend.entity.SavedArticle;
import com.safetica.safetica_backend.service.SavedArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-articles")
public class SavedArticleController {

    private final SavedArticleService savedArticleService;

    public SavedArticleController(SavedArticleService savedArticleService) {
        this.savedArticleService = savedArticleService;
    }

    // ❗ DTO ile zenginleştirilmiş endpoint
    @GetMapping("/{userId}/dtos")
    public ResponseEntity<List<SavedArticleDTO>> getSavedArticleDTOs(@PathVariable Long userId) {
        List<SavedArticleDTO> dtos = savedArticleService.getSavedArticleDTOsByUserId(userId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<SavedArticleDTO>> getSavedArticles(@PathVariable Long userId) {
        List<SavedArticleDTO> articles = savedArticleService.getSavedArticleDTOsByUserId(userId);
        return ResponseEntity.ok(articles);
    }

    @PostMapping("/add")
    public ResponseEntity<SavedArticle> saveArticle(@RequestParam Long userId, @RequestParam Long postId) {
        SavedArticle article = savedArticleService.saveArticle(userId, postId);
        return ResponseEntity.ok(article);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> unsaveArticle(@RequestParam Long userId, @RequestParam Long postId) {
        savedArticleService.unsaveArticle(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/is-saved")
    public ResponseEntity<Boolean> isArticleSaved(@RequestParam Long userId, @RequestParam Long postId) {
        boolean saved = savedArticleService.isArticleSaved(userId, postId);
        return ResponseEntity.ok(saved);
    }
}
