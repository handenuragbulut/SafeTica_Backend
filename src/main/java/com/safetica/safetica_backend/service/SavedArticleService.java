// src/main/java/com/safetica/safetica_backend/service/SavedArticleService.java
package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.entity.SavedArticle;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.repository.SavedArticleRepository;
import com.safetica.safetica_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SavedArticleService {

    @Autowired private SavedArticleRepository savedArticleRepo;
    @Autowired private UserRepository userRepo;

    /** Kullanıcının kaydettiği tüm makaleleri getir */
    public List<SavedArticle> listSavedArticles(Long userId) {
        return savedArticleRepo.findAllByUserId(userId);
    }

    /** Makaleyi kaydet */
    public SavedArticle saveArticle(Long userId, Long articleId) {
        User u = userRepo.findById(userId)
                         .orElseThrow(() -> new IllegalArgumentException("User not found"));
        SavedArticle sa = new SavedArticle();
        sa.setUser(u);
        sa.setArticleId(articleId);
        sa.setSavedAt(LocalDateTime.now());
        return savedArticleRepo.save(sa);
    }

    /** Kaydı kaldır */
    public void removeSavedArticle(Long userId, Long articleId) {
        savedArticleRepo.deleteByUserIdAndArticleId(userId, articleId);
    }
}
