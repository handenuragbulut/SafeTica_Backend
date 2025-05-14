package com.safetica.safetica_backend.repository;

import com.safetica.safetica_backend.entity.BrandRepresentative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepresentativeRepository extends JpaRepository<BrandRepresentative, Long> {

    // Onay durumuna göre filtreleme
    List<BrandRepresentative> findByStatus(String status);

    // Email ile bulmak için
    BrandRepresentative findByContactEmail(String contactEmail);
}
