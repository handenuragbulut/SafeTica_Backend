package com.safetica.safetica_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_articles")
public class SavedArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kullanıcı ile ilişki (ManyToOne)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Makale ile ilişki (ManyToOne)
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private BlogPost post;

    @Column(name = "saved_at")
    private LocalDateTime savedAt = LocalDateTime.now();

    // --- Getter & Setter'lar ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BlogPost getPost() {
        return post;
    }

    public void setPost(BlogPost post) {
        this.post = post;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
}
