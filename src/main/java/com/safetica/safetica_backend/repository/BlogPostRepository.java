package com.safetica.safetica_backend.repository;

import com.safetica.safetica_backend.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    // Şu an özel sorguya gerek yok, JpaRepository tüm temel CRUD işlemlerini sağlar
}
