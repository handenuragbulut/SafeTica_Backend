package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.dto.SavedArticleDTO;
import com.safetica.safetica_backend.entity.BlogPost;
import com.safetica.safetica_backend.entity.SavedArticle;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.repository.BlogPostRepository;
import com.safetica.safetica_backend.repository.SavedArticleRepository;
import com.safetica.safetica_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
//import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SavedArticleService {

    private final SavedArticleRepository savedArticleRepository;
    private final BlogPostRepository blogPostRepository;

    @Autowired
    private UserRepository userRepository;

    public SavedArticleService(SavedArticleRepository savedArticleRepository,
            BlogPostRepository blogPostRepository) {
        this.savedArticleRepository = savedArticleRepository;
        this.blogPostRepository = blogPostRepository;
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    public List<SavedArticle> getSavedArticlesByUserId(Long userId) {
        return savedArticleRepository.findByUser_Id(userId);
    }

    public boolean isArticleSaved(Long userId, Long postId) {
        return savedArticleRepository.findByUser_IdAndPost_Id(userId, postId).isPresent();
    }

    public SavedArticle saveArticle(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        SavedArticle article = new SavedArticle();
        article.setUser(user);
        article.setPost(post);
        article.setSavedAt(LocalDateTime.now());

        return savedArticleRepository.save(article);
    }

    public void unsaveArticle(Long userId, Long postId) {
    savedArticleRepository.findByUser_IdAndPost_Id(userId, postId).ifPresentOrElse(
        article -> {
            savedArticleRepository.delete(article);
            System.out.println("Silindi -> userId: " + userId + ", postId: " + postId);
        },
        () -> {
            System.out.println("Silinecek kayıt bulunamadı -> userId: " + userId + ", postId: " + postId);
            // Buraya throw new RuntimeException(...) koymak istemiyorsan boş bırak.
        }
    );
}


    public List<SavedArticleDTO> getSavedArticleDTOsByUserId(Long userId) {
        List<SavedArticle> savedArticles = savedArticleRepository.findByUser_Id(userId);

        return savedArticles.stream()
                .map(savedArticle -> {
                    BlogPost blogPost = savedArticle.getPost();
                    return new SavedArticleDTO(
                            blogPost.getId(),
                            blogPost.getTitle(),
                            blogPost.getShortDescription(),
                            blogPost.getImageUrl());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
