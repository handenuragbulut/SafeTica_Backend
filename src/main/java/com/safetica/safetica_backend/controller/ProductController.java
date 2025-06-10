package com.safetica.safetica_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetica.safetica_backend.entity.Product;
import com.safetica.safetica_backend.entity.UserPreference;
import com.safetica.safetica_backend.service.ProductService;
import com.safetica.safetica_backend.service.UserPreferenceService;
import com.safetica.safetica_backend.service.UserService;
import com.safetica.safetica_backend.dto.ScanRequestDTO;
import com.safetica.safetica_backend.dto.ScanResultDTO;
import com.safetica.safetica_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPreferenceService userPreferenceService;

    // @GetMapping
    // public List<Product> getAllProducts() {
    // return productService.getAllProducts();
    // }

    @GetMapping
    public ResponseEntity<List<Product>> getApprovedProducts() {
        List<Product> approvedProducts = productService.getApprovedProducts();
        return ResponseEntity.ok(approvedProducts);
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
            @RequestParam(value = "Certifications", required = false, defaultValue = "") List<String> certifications) {
        List<Product> filteredProducts = productService.filterProducts(
                categories.isEmpty() ? null : categories,
                subCategories.isEmpty() ? null : subCategories,
                brands.isEmpty() ? null : brands,
                allergies.isEmpty() ? null : allergies,
                includeIngredients.isEmpty() ? null : includeIngredients,
                excludeIngredients.isEmpty() ? null : excludeIngredients,
                certifications.isEmpty() ? null : certifications);
        return ResponseEntity.ok(filteredProducts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService
                .getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/home")
    public List<Product> getTopProductsForHome() {
        return productService.getTopProductsForHome();
    }

    @GetMapping("/brands")
    public ResponseEntity<List<String>> getAllBrands() {
        List<String> brands = productService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProductById(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return productService.updateProduct(id, updatedProduct)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanResultDTO> scanProductAndCheckAllergies(
            @RequestBody ScanRequestDTO scanRequest,
            HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        String email = jwtUtil.getEmailFromToken(token);

        return userService.findByEmail(email)
                .map(user -> {
                    ScanResultDTO result = productService.scanProductAndCheckAllergies(
                            scanRequest.getText(), user.getId());
                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/scan-ingredients")
    public ResponseEntity<ScanResultDTO> scanIngredientsAndCheckAllergies(
            @RequestBody ScanRequestDTO scanRequest,
            HttpServletRequest request) {

        String token = jwtUtil.extractTokenFromRequest(request);
        String email = jwtUtil.getEmailFromToken(token);

        return userService.findByEmail(email)
                .map(user -> {
                    List<String> userAllergies = userPreferenceService.getPreferences(user.getId()).getAllergies();
                    List<String> matchedAllergies = userAllergies.stream()
                            .filter(allergy -> scanRequest.getText().toLowerCase().contains(allergy.toLowerCase()))
                            .collect(Collectors.toList());

                    ScanResultDTO result = new ScanResultDTO();
                    result.setMatchedAllergies(matchedAllergies);
                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/scan-ingredients-with-excludes")
    public ResponseEntity<ScanResultDTO> scanIngredientsAndCheckAllergiesAndExcludes(
            @RequestBody ScanRequestDTO scanRequest,
            HttpServletRequest request) {

        String token = jwtUtil.extractTokenFromRequest(request);
        String email = jwtUtil.getEmailFromToken(token);

        return userService.findByEmail(email)
                .map(user -> {
                    // 1️⃣ Kullanıcının alerji ve istenmeyen içerik listelerini çek
                    List<String> userAllergies = userPreferenceService.getPreferences(user.getId()).getAllergies();
                    List<String> userExcludes = userPreferenceService.getPreferences(user.getId())
                            .getExcludedIngredients();

                    // 2️⃣ Alerji eşleşmeleri
                    List<String> matchedAllergies = null;
                    if (userAllergies != null) {
                        matchedAllergies = userAllergies.stream()
                                .filter(allergy -> scanRequest.getText().toLowerCase().contains(allergy.toLowerCase()))
                                .collect(Collectors.toList());
                    }

                    // 3️⃣ İstenmeyen içerik eşleşmeleri
                    List<String> matchedExcludes = null;
                    if (userExcludes != null) {
                        matchedExcludes = userExcludes.stream()
                                .filter(exclude -> scanRequest.getText().toLowerCase().contains(exclude.toLowerCase()))
                                .collect(Collectors.toList());
                    }

                    // 4️⃣ DTO hazırla
                    ScanResultDTO result = new ScanResultDTO();
                    result.setMatchedAllergies(matchedAllergies);
                    result.setMatchedExcludedIngredients(matchedExcludes);

                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}