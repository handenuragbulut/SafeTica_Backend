package com.safetica.safetica_backend.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "user_preferences")
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ElementCollection
    @CollectionTable(name = "user_allergies", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "allergy")
    private List<String> allergies;

    @ElementCollection
    @CollectionTable(name = "user_excluded_ingredients", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "excluded_ingredient")
    private List<String> excludedIngredients;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());

    // --- GETTER / SETTER ---

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public List<String> getAllergies() { return allergies; }

    public void setAllergies(List<String> allergies) { this.allergies = allergies; }

    public List<String> getExcludedIngredients() { return excludedIngredients; }

    public void setExcludedIngredients(List<String> excludedIngredients) { this.excludedIngredients = excludedIngredients; }

    public Timestamp getCreatedAt() { return createdAt; }

    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
