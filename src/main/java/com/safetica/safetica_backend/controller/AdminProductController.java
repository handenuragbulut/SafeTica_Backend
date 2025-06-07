package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.entity.Product;
import com.safetica.safetica_backend.repository.ProductRepository;
import com.safetica.safetica_backend.service.ActivityLogService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductRepository productRepository;
    private final ActivityLogService activityLogService;

    public AdminProductController(ProductRepository productRepository, ActivityLogService activityLogService) {
        this.productRepository = productRepository;
        this.activityLogService = activityLogService;
    }

    // ✅ Onay bekleyen ürünleri listeler
    @GetMapping("/pending")
    public ResponseEntity<List<Product>> getPendingProducts() {
        List<Product> pendingProducts = productRepository.findByStatus("PENDING");
        return ResponseEntity.ok(pendingProducts);
    }

    // ✅ Ürünü onaylar
    @PatchMapping("/{productId}/approve")
    public ResponseEntity<Product> approveProduct(@PathVariable Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setStatus("APPROVED");
            productRepository.save(product);
             activityLogService.log("approved", "Product approved: " + product.getName(), null);
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Ürünü reddeder
    @PatchMapping("/{productId}/reject")
    public ResponseEntity<Product> rejectProduct(@PathVariable Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setStatus("REJECTED");
            productRepository.save(product);
            activityLogService.log("rejected", "Product rejected: " + product.getName(), null);
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/representative")
    public ResponseEntity<List<Product>> getRepresentativeProducts(
            @RequestParam String status) {
        List<Product> products = productRepository.findBySubmittedByRepresentativeIdIsNotNullAndStatus(status);
        return ResponseEntity.ok(products);
    }
}
