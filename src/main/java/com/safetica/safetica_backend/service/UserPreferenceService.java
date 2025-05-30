package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.dto.UserPreferenceDTO;
import com.safetica.safetica_backend.entity.UserPreference;
import com.safetica.safetica_backend.repository.UserPreferenceRepository;
import com.safetica.safetica_backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class UserPreferenceService {

    private final UserPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public UserPreferenceService(UserPreferenceRepository preferenceRepository, UserRepository userRepository) {
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    // ✅ Hem ekleme hem güncelleme yapan tek method
    public void savePreferences(Long userId, UserPreferenceDTO dto) {
        UserPreference preference = preferenceRepository.findByUserId(userId)
                .orElse(new UserPreference()); // yoksa yeni oluştur

        preference.setUserId(userId);
        preference.setAllergies(dto.getAllergies());
        preference.setExcludedIngredients(dto.getExcludedIngredients());

        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (preference.getId() == null) {
            preference.setCreatedAt(now);
        }
        preference.setUpdatedAt(now);

        preferenceRepository.save(preference);

        userRepository.findById(userId).ifPresent(user -> {
            user.setPreferencesCompleted(true); // 🔥 Profil tamamlandı
            userRepository.save(user);
        });
    }

    // ✅ Getirme
    public UserPreferenceDTO getPreferences(Long userId) {
        Optional<UserPreference> preference = preferenceRepository.findByUserId(userId);
        if (preference.isPresent()) {
            UserPreference pref = preference.get();
            UserPreferenceDTO dto = new UserPreferenceDTO();
            dto.setAllergies(pref.getAllergies());
            dto.setExcludedIngredients(pref.getExcludedIngredients());
            return dto;
        }
        return new UserPreferenceDTO(); // kullanıcıda veri yoksa boş DTO dön
    }
}
