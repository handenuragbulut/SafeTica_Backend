package com.safetica.safetica_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetica.safetica_backend.entity.Product;
import com.safetica.safetica_backend.service.ProductService;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String query) {
        return productService.searchProducts(query);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(
        @RequestParam(value = "category", required = false, defaultValue = "") List<String> categories,
        @RequestParam(value = "subCategory", required = false, defaultValue = "") List<String> subCategories,
        @RequestParam(value = "Brand", required = false, defaultValue = "") List<String> brands,
        @RequestParam(value = "Allergies", required = false, defaultValue = "") List<String> allergies,
        @RequestParam(value = "IncludeIngredients", required = false, defaultValue = "") List<String> includeIngredients,
        @RequestParam(value = "ExcludeIngredients", required = false, defaultValue = "") List<String> excludeIngredients,
        @RequestParam(value = "Certifications", required = false, defaultValue = "") List<String> certifications
    ) {
        List<Product> filteredProducts = productService.filterProducts(
            categories.isEmpty() ? null : categories,
            subCategories.isEmpty() ? null : subCategories,
            brands.isEmpty() ? null : brands,
            allergies.isEmpty() ? null : allergies,
            includeIngredients.isEmpty() ? null : includeIngredients,
            excludeIngredients.isEmpty() ? null : excludeIngredients,
            certifications.isEmpty() ? null : certifications
        );
        return ResponseEntity.ok(filteredProducts);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService
                .getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    
}