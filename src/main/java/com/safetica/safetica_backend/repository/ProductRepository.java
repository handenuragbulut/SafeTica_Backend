package com.safetica.safetica_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.safetica.safetica_backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
        List<Product> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrSubCategoryContainingIgnoreCase(

                        String name, String brand, String category, String subCategory);

        // Filtreleme işlemi için özel bir sorgu
        @Query("SELECT p FROM Product p " +
                        "WHERE (:categories IS NULL OR p.category IN :categories) " +
                        "AND (:subCategories IS NULL OR p.subCategory IN :subCategories) " +
                        "AND (:brands IS NULL OR p.brand IN :brands) " +
                        "AND (:allergies IS NULL OR NOT EXISTS " +
                        "(SELECT a FROM Product a WHERE a.id = p.id AND a.ingredients LIKE CONCAT('%', :allergies, '%'))) "
                        +
                        "AND (:includeIngredients IS NULL OR EXISTS " +
                        "(SELECT i FROM Product i WHERE i.id = p.id AND i.ingredients LIKE CONCAT('%', :includeIngredients, '%'))) "
                        +
                        "AND (:excludeIngredients IS NULL OR NOT EXISTS " +
                        "(SELECT e FROM Product e WHERE e.id = p.id AND e.ingredients LIKE CONCAT('%', :excludeIngredients, '%'))) "
                        +
                        "AND (:certifications IS NULL OR p.certifications IN :certifications)")
        List<Product> filterProducts(
                        @Param("categories") List<String> categories,
                        @Param("subCategories") List<String> subCategories,
                        @Param("brands") List<String> brands,
                        @Param("allergies") String allergies,
                        @Param("includeIngredients") String includeIngredients,
                        @Param("excludeIngredients") String excludeIngredients,
                        @Param("certifications") List<String> certifications);

        List<Product> findByCategoryInAndSubCategoryIn(
                        List<String> categories,
                        List<String> subCategories);

        // ✅ Status'a göre ürünleri getiren metot
        List<Product> findByStatus(String status);

        List<Product> findBySubmittedByRepresentativeIdIsNotNullAndStatus(String status);

}
