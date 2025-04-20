// FavoriteRepository.java
package com.safetica.safetica_backend.repository;

import com.safetica.safetica_backend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByUserId(Long userId);
    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);

}
