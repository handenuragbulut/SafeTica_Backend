package com.safetica.safetica_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetica.safetica_backend.entity.Product;
import com.safetica.safetica_backend.repository.ProductRepository;

import com.safetica.safetica_backend.dto.ScanResultDTO;
import com.safetica.safetica_backend.repository.UserPreferenceRepository;
import com.safetica.safetica_backend.entity.UserPreference;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getApprovedProducts() {
        return productRepository.findByStatus("APPROVED");
    }

    public List<Product> searchProducts(String query) {
        return productRepository
                .findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrSubCategoryContainingIgnoreCase(
                        query, query, query, query);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getTopProductsForHome() {
        return productRepository.findAll()
                .stream()
                .limit(16)
                .collect(Collectors.toList());
    }

    public List<Product> filterProducts(
            List<String> categories,
            List<String> subCategories,
            List<String> brands,
            List<String> allergies,
            List<String> includeIngredients,
            List<String> excludeIngredients,
            List<String> certifications) {
        return productRepository.findAll().stream()
                .filter(product -> categories == null || categories.contains(product.getCategory()))
                .filter(product -> subCategories == null || subCategories.contains(product.getSubCategory()))
                .filter(product -> brands == null || brands.contains(product.getBrand()))
                .filter(product -> certifications == null || certifications.stream()
                        .allMatch(cert -> product.getCertifications().contains(cert)))
                .filter(product -> includeIngredients == null || includeIngredients.stream()
                        .allMatch(ingredient -> product.getIngredients().contains(ingredient)))
                .filter(product -> excludeIngredients == null || excludeIngredients.stream()
                        .noneMatch(ingredient -> product.getIngredients().contains(ingredient)))
                .filter(product -> allergies == null || allergies.stream()
                        .noneMatch(allergy -> product.getIngredients().contains(allergy)))
                .collect(Collectors.toList());
    }

    public List<String> getAllBrands() {
        return productRepository.findAll().stream()
                .map(Product::getBrand)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return productRepository.findAll().stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public boolean deleteProductById(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setBrand(updatedProduct.getBrand());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setIngredients(updatedProduct.getIngredients());
            return productRepository.save(existingProduct);
        });
    }

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    public ScanResultDTO scanProductAndCheckAllergies(String scannedText, Long userId) {
        List<Product> products = productRepository.findAll();
        String lowerScannedText = scannedText.toLowerCase();

        // 1️⃣ Önce tam eşleşme
        Product exactMatchedProduct = products.stream()
                .filter(product -> lowerScannedText.contains(product.getName().toLowerCase())
                        && lowerScannedText.contains(product.getBrand().toLowerCase()))
                .findFirst()
                .orElse(null);

        // 2️⃣ Skor bazlı eşleşme
        AtomicReference<Product> finalMatchedProductRef = new AtomicReference<>(exactMatchedProduct);
        AtomicReference<Integer> highestScoreRef = new AtomicReference<>(0);

        if (exactMatchedProduct == null) {
            products.forEach(product -> {
                int score = 0;
                if (lowerScannedText.contains(product.getName().toLowerCase())) {
                    score += 10;
                }
                if (lowerScannedText.contains(product.getBrand().toLowerCase())) {
                    score += 5;
                }
                if (product.getDescription() != null &&
                        lowerScannedText.contains(product.getDescription().toLowerCase())) {
                    score += 3;
                }
                if (score > highestScoreRef.get()) {
                    highestScoreRef.set(score);
                    finalMatchedProductRef.set(product);
                }
            });
        }

        Product finalMatchedProduct = finalMatchedProductRef.get();

        // 3️⃣ Kullanıcı alerji ve istenmeyen içeriklerini çek
        List<String> userAllergies = userPreferenceRepository.findByUserId(userId)
                .map(UserPreference::getAllergies)
                .orElse(null);

        List<String> userExcludedIngredients = userPreferenceRepository.findByUserId(userId)
                .map(UserPreference::getExcludedIngredients)
                .orElse(null);

        // 4️⃣ Eşleşen alerjileri bul
        List<String> matchedAllergies = null;
        if (finalMatchedProduct != null && userAllergies != null) {
            matchedAllergies = userAllergies.stream()
                    .filter(allergy -> finalMatchedProduct.getIngredients() != null &&
                            finalMatchedProduct.getIngredients().toLowerCase().contains(allergy.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // 5️⃣ Eşleşen istenmeyen içerikleri bul
        List<String> matchedExcludedIngredients = null;
        if (finalMatchedProduct != null && userExcludedIngredients != null) {
            matchedExcludedIngredients = userExcludedIngredients.stream()
                    .filter(exclude -> finalMatchedProduct.getIngredients() != null &&
                            finalMatchedProduct.getIngredients().toLowerCase().contains(exclude.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // 6️⃣ DTO oluştur
        ScanResultDTO result = new ScanResultDTO();
        result.setProduct(finalMatchedProduct);
        result.setMatchedAllergies(matchedAllergies);
        result.setMatchedExcludedIngredients(matchedExcludedIngredients);

        return result;
    }

}