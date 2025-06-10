package com.safetica.safetica_backend.dto;

import com.safetica.safetica_backend.entity.Product;
import java.util.List;

public class ScanResultDTO {
    private Product product;
    private List<String> matchedAllergies;
    private List<String> matchedExcludedIngredients;

    // Getter & Setter
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<String> getMatchedAllergies() {
        return matchedAllergies;
    }

    public void setMatchedAllergies(List<String> matchedAllergies) {
        this.matchedAllergies = matchedAllergies;
    }

    public List<String> getMatchedExcludedIngredients() {
        return matchedExcludedIngredients;
    }

    public void setMatchedExcludedIngredients(List<String> matchedExcludedIngredients) {
        this.matchedExcludedIngredients = matchedExcludedIngredients;
    }
}
