package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.entity.Product;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.repository.ProductRepository;
import com.safetica.safetica_backend.service.UserService;
import com.safetica.safetica_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/representative/products")
public class RepresentativeProductController {

    private final ProductRepository productRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public RepresentativeProductController(ProductRepository productRepository,
            UserService userService,
            JwtUtil jwtUtil) {
        this.productRepository = productRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Ürün ekleme isteği - PENDING olarak kaydeder
    @PostMapping("/request")
    public ResponseEntity<?> submitProductRequest(@RequestBody Product product, HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token missing");
        }

        String email = jwtUtil.getEmailFromToken(token);
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        User user = userOpt.get();
        if (!user.getRole().equals("BRAND_REPRESENTATIVE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only representatives can submit product requests");
        }

        product.setSubmittedByRepresentativeId(user.getId()); // ✅ Ürün kimin tarafından eklendi
        product.setStatus("PENDING");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    // ✅ Giriş yapan temsilcinin sadece kendi ürünlerini listelemesi
    @GetMapping("/my-products")
    public ResponseEntity<List<Product>> getMyProducts(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.getEmailFromToken(token);
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = userOpt.get().getId();
        List<Product> products = productRepository.findBySubmittedByRepresentativeId(userId);
        return ResponseEntity.ok(products);
    }
}
