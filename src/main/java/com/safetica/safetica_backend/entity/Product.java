package com.safetica.safetica_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String category;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(length = 1000)
    private String description;

    @Column(length = 2000)
    private String ingredients;

    @Column(length = 1000)
    private String certifications;

    @Column(nullable = false)
    private String status = "APPROVED"; // PENDING, APPROVED, REJECTED

    @Column(name = "submitted_by_representative_id", nullable = true)
    private Long submittedByRepresentativeId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private String imageUrl;

    // Constructors
    public Product() {
    }

    public Product(String name, String brand, String category, String subCategory, String description,
            String ingredients, String certifications, LocalDateTime createdAt,
            LocalDateTime updatedAt, String imageUrl) {
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.subCategory = subCategory;
        this.description = description;
        this.ingredients = ingredients;
        this.certifications = certifications;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imageUrl = imageUrl;
    }

    // PrePersist and PreUpdate for Timestamps (Optional)
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    // ... (Aynı şekilde tüm alanlar için mevcut getter/setter'lar)
    // imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Brand
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    // Category
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // SubCategory
    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    // Description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Ingredients
    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    // Certifications
    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    // CreatedAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // UpdatedAt
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getSubmittedByRepresentativeId() {
        return submittedByRepresentativeId;
    }

    public void setSubmittedByRepresentativeId(Long submittedByRepresentativeId) {
        this.submittedByRepresentativeId = submittedByRepresentativeId;
    }


    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", description='" + description + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", certifications='" + certifications + '\'' +
                ", status='" + status + '\'' +
                ", submittedByRepresentativeId=" + submittedByRepresentativeId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
