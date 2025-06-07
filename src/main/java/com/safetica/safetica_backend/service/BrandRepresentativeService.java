package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.entity.BrandRepresentative;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.repository.BrandRepresentativeRepository;
import com.safetica.safetica_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandRepresentativeService {

    private final BrandRepresentativeRepository brandRepRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BrandRepresentativeService(BrandRepresentativeRepository brandRepRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.brandRepRepository = brandRepRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Başvuru kaydet + kullanıcıyı user_table'a pasif olarak ekle
    public BrandRepresentative apply(BrandRepresentative rep, String rawPassword) {
        BrandRepresentative savedRep = brandRepRepository.save(rep);

        if (userRepository.findByEmail(rep.getContactEmail()).isEmpty()) {
            User user = new User();
            user.setEmail(rep.getContactEmail());
            user.setFirstName(rep.getFirstName());
            user.setLastName(rep.getLastName());
            user.setCountry(rep.getCountry());
            user.setPhoneNumber(rep.getPhoneNumber());
            user.setRole("BRAND_REPRESENTATIVE");
            user.setActive(false); // Admin onayı bekliyor
            user.setPasswordHash(passwordEncoder.encode(rawPassword));

            userRepository.save(user);
        }

        return savedRep;
    }

    // ✅ Onay
    public Optional<BrandRepresentative> approve(Long id) {
        Optional<BrandRepresentative> optionalRep = brandRepRepository.findById(id);
        if (optionalRep.isPresent()) {
            BrandRepresentative rep = optionalRep.get();
            rep.setStatus("APPROVED");
            brandRepRepository.save(rep);

            userRepository.findByEmail(rep.getContactEmail()).ifPresent(user -> {
                user.setActive(true); // Kullanıcıyı aktif hale getir
                userRepository.save(user);
            });

            return Optional.of(rep);
        }
        return Optional.empty();
    }

    // ✅ Reddet
    public Optional<BrandRepresentative> reject(Long id) {
        Optional<BrandRepresentative> optionalRep = brandRepRepository.findById(id);
        if (optionalRep.isPresent()) {
            BrandRepresentative rep = optionalRep.get();
            rep.setStatus("REJECTED");
            brandRepRepository.save(rep);

            userRepository.findByEmail(rep.getContactEmail()).ifPresent(user -> {
                userRepository.delete(user); // İstersen pasifleştirme de yapılabilir
            });

            return Optional.of(rep);
        }
        return Optional.empty();
    }

    public List<BrandRepresentative> getAll() {
        return brandRepRepository.findAll();
    }

    public List<BrandRepresentative> listByStatus(String status) {
        return brandRepRepository.findByStatus(status);
    }

    public Optional<BrandRepresentative> getById(Long id) {
        return brandRepRepository.findById(id);
    }

    public Optional<BrandRepresentative> update(Long id, BrandRepresentative updated) {
        return brandRepRepository.findById(id).map(existing -> {
            existing.setFirstName(updated.getFirstName());
            existing.setLastName(updated.getLastName());
            existing.setCompanyName(updated.getCompanyName());
            existing.setBrandName(updated.getBrandName());
            existing.setContactEmail(updated.getContactEmail());
            existing.setPhoneNumber(updated.getPhoneNumber());
            existing.setCountry(updated.getCountry());
            return brandRepRepository.save(existing);
        });
    }

    public void deleteRepresentative(Long id) {
        brandRepRepository.deleteById(id);
    }
}
