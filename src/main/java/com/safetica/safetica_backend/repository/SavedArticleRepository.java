// SavedArticleRepository.java
package com.safetica.safetica_backend.repository;

import com.safetica.safetica_backend.entity.SavedArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SavedArticleRepository extends JpaRepository<SavedArticle, Long> {
    List<SavedArticle> findAllByUserId(Long userId);
    void deleteByUserIdAndArticleId(Long userId, Long articleId);
}
