// FavoriteService.java
package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.entity.Favorite;
import com.safetica.safetica_backend.entity.Product;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.repository.FavoriteRepository;
import com.safetica.safetica_backend.repository.ProductRepository;
import com.safetica.safetica_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ProductRepository productRepo;

    public List<Favorite> listFavorites(Long userId) {
        return favoriteRepo.findAllByUserId(userId);
    }

    public Favorite addFavorite(Long userId, Long productId) {
        User u = userRepo.findById(userId).orElseThrow();
        Product p = productRepo.findById(productId).orElseThrow();
        Favorite f = new Favorite();
        f.setUser(u);
        f.setProduct(p);
        return favoriteRepo.save(f);
    }

    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        favoriteRepo.deleteByUser_IdAndProduct_Id(userId, productId);
    }

}
