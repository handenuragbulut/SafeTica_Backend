package com.safetica.safetica_backend.dto;

public class SavedArticleDTO {
    private Long postId;
    private String title;
    private String shortDescription;
    private String imageUrl;

    public SavedArticleDTO(Long postId, String title, String shortDescription, String imageUrl) {
        this.postId = postId;
        this.title = title;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
