package com.safetica.safetica_backend.repository;

import com.safetica.safetica_backend.entity.SavedArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedArticleRepository extends JpaRepository<SavedArticle, Long> {
    List<SavedArticle> findByUser_Id(Long userId);
    Optional<SavedArticle> findByUser_IdAndPost_Id(Long userId, Long postId);
    void deleteByUser_IdAndPost_Id(Long userId, Long postId);
}
