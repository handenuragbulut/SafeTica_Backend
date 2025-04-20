// src/main/java/com/safetica/safetica_backend/service/UserProfileService.java
package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.entity.UserProfile;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.repository.UserProfileRepository;
import com.safetica.safetica_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired private UserProfileRepository profileRepo;
    @Autowired private UserRepository userRepo;

    /** Kullanıcının profilini getir */
    public Optional<UserProfile> getByUserId(Long userId) {
        return profileRepo.findByUserId(userId);
    }
    public void deleteByUserId(Long userId) {
        profileRepo.findByUserId(userId)
                   .ifPresent(profileRepo::delete);
    }

    /** Profili oluştur veya güncelle */
    public UserProfile saveOrUpdate(Long userId, UserProfile incoming) {
        User u = userRepo.findById(userId)
                         .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserProfile profile = profileRepo.findByUserId(userId)
                                         .orElseGet(() -> {
                                           UserProfile np = new UserProfile();
                                           np.setUser(u);
                                           return np;
                                         });
        // İleride eklenecek alanları burada set edebilirsin:
        profile.setSkinType(incoming.getSkinType());
        profile.setAllergies(incoming.getAllergies());
        profile.setSurveyDone(incoming.getSurveyDone());
        return profileRepo.save(profile);
    }
}
