package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.entity.Product;
import com.safetica.safetica_backend.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/representative/products")
public class RepresentativeProductController {

    private final ProductRepository productRepository;

    public RepresentativeProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ✅ Ürün ekleme isteğini alır ve PENDING olarak kaydeder
    @PostMapping("/request")
    public ResponseEntity<Product> submitProductRequest(@RequestBody Product productRequest) {
        productRequest.setStatus("PENDING");  // Ürün başlangıçta "PENDING" olarak işaretlenir
        Product savedProduct = productRepository.save(productRequest);
        return ResponseEntity.ok(savedProduct);
    }
}
