package com.safetica.safetica_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.safetica.safetica_backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
