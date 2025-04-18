package com.safetica.safetica_backend.repository;

import com.safetica.safetica_backend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    // Custom query methods can be defined here if needed
    
}
