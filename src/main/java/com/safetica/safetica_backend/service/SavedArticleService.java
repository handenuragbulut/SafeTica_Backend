package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.dto.SavedArticleDTO;
import com.safetica.safetica_backend.entity.BlogPost;
import com.safetica.safetica_backend.entity.SavedArticle;
import com.safetica.safetica_backend.repository.BlogPostRepository;
import com.safetica.safetica_backend.repository.SavedArticleRepository;
import com.safetica.safetica_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SavedArticleService {

    private final SavedArticleRepository savedArticleRepository;
    private final BlogPostRepository blogPostRepository;

    public SavedArticleService(SavedArticleRepository savedArticleRepository,
            BlogPostRepository blogPostRepository) {
        this.savedArticleRepository = savedArticleRepository;
        this.blogPostRepository = blogPostRepository;
    }

    @Autowired
    private UserRepository userRepository; // ya da kendi entity sınıfına göre

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    public List<SavedArticle> getSavedArticlesByUserId(Long userId) {
        return savedArticleRepository.findByUserId(userId);
    }

    public boolean isArticleSaved(Long userId, Long postId) {
        return savedArticleRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }

    public SavedArticle saveArticle(Long userId, Long postId) {
        SavedArticle article = new SavedArticle();
        article.setUserId(userId);
        article.setPostId(postId);
        article.setSavedAt(LocalDateTime.now());
        article.setCreatedAt(LocalDateTime.now());
        return savedArticleRepository.save(article);
    }

    public void unsaveArticle(Long userId, Long postId) {
        savedArticleRepository.deleteByUserIdAndPostId(userId, postId);
    }

    public List<SavedArticleDTO> getSavedArticleDTOsByUserId(Long userId) {
        List<SavedArticle> savedArticles = savedArticleRepository.findByUserId(userId);

        return savedArticles.stream()
                .map(savedArticle -> {
                    Optional<BlogPost> blogPostOpt = blogPostRepository.findById(savedArticle.getPostId());
                    if (blogPostOpt.isPresent()) {
                        BlogPost blogPost = blogPostOpt.get();
                        return new SavedArticleDTO(
                                blogPost.getId(),
                                blogPost.getTitle(),
                                blogPost.getShortDescription(),
                                blogPost.getImageUrl());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
