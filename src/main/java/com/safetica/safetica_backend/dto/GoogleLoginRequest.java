package com.safetica.safetica_backend.dto;

public class GoogleLoginRequest {
    private String googleIdToken;

    // Getters and Setters
    public String getGoogleIdToken() {
        return googleIdToken;
    }

    public void setGoogleIdToken(String googleIdToken) {
        this.googleIdToken = googleIdToken;
    }
}
