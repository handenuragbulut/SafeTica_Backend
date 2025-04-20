// UserProfileRepository.java
package com.safetica.safetica_backend.repository;

import com.safetica.safetica_backend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
}
