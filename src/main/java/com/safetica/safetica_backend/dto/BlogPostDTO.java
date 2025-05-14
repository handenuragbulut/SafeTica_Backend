package com.safetica.safetica_backend.dto;

import java.time.LocalDateTime;

public class BlogPostDTO {
    private Long id;
    private String title;
    private String shortDescription;
    private String imageUrl;
    private String content;
    private LocalDateTime publishedAt;

    // Constructor
    public BlogPostDTO(Long id, String title, String shortDescription, String imageUrl, String content, LocalDateTime publishedAt) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.content = content;
        this.publishedAt = publishedAt;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}
