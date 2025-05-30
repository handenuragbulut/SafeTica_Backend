package com.safetica.safetica_backend.dto;

import java.util.List;

public class UserPreferenceDTO {
    private List<String> allergies;
    private List<String> excludedIngredients;

    // Constructors
    public UserPreferenceDTO() {}

    public UserPreferenceDTO(List<String> allergies, List<String> excludedIngredients) {
        this.allergies = allergies;
        this.excludedIngredients = excludedIngredients;
    }

    // Getters & Setters
    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    public List<String> getExcludedIngredients() {
        return excludedIngredients;
    }

    public void setExcludedIngredients(List<String> excludedIngredients) {
        this.excludedIngredients = excludedIngredients;
    }
}
