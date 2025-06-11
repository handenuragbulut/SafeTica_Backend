package com.safetica.safetica_backend.dto;

public class UserRegistrationRequest {
    private String email;
    private String password;

    // Getter ve Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
