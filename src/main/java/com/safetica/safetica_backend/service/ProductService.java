package com.safetica.safetica_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetica.safetica_backend.entity.Product;
import com.safetica.safetica_backend.repository.ProductRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Optional;

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

}