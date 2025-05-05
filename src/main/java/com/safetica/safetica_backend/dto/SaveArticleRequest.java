package com.safetica.safetica_backend.dto;

public class SaveArticleRequest {

    private Long userId;
    private Long postId;

    public SaveArticleRequest() {
    }

    public SaveArticleRequest(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
