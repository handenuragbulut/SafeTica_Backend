// src/main/java/com/safetica/safetica_backend/controller/FavoriteController.java
package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.entity.Favorite;
import com.safetica.safetica_backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.safetica.safetica_backend.dto.FavoriteRequest;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /** Kullanıcının favori ürünlerini döner */
    @GetMapping("/{userId}")
    public ResponseEntity<List<Favorite>> listFavorites(@PathVariable Long userId) {
        List<Favorite> favs = favoriteService.listFavorites(userId);
        return ResponseEntity.ok(favs);
    }

    /** Yeni bir favori ekler */
    @PostMapping
    public ResponseEntity<Favorite> addFavorite(@RequestBody FavoriteRequest request) {
        Long userId = request.getUserId();
        Long productId = request.getProductId();
        Favorite f = favoriteService.addFavorite(userId, productId);
        return ResponseEntity.ok(f);
    }

    /** Bir favoriyi kaldırır */
    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        favoriteService.removeFavorite(userId, productId);
        return ResponseEntity.noContent().build();
    }
}
