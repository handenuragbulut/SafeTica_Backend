package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.entity.BrandRepresentative;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.repository.BrandRepresentativeRepository;
import com.safetica.safetica_backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandRepresentativeService {

    private final BrandRepresentativeRepository brandRepRepository;
    private final UserRepository userRepository;

    public BrandRepresentativeService(BrandRepresentativeRepository brandRepRepository, UserRepository userRepository) {
        this.brandRepRepository = brandRepRepository;
        this.userRepository = userRepository;
    }

    // ✅ Başvuru işlemi
    public BrandRepresentative apply(BrandRepresentative representative) {
        return brandRepRepository.save(representative);
    }

    // ✅ Statüye göre listeleme
    public List<BrandRepresentative> listByStatus(String status) {
        return brandRepRepository.findByStatus(status);
    }

    // ✅ Onaylama işlemi ve User hesabı oluşturma
    public Optional<BrandRepresentative> approve(Long id) {
        Optional<BrandRepresentative> optionalRep = brandRepRepository.findById(id);
        if (optionalRep.isPresent()) {
            BrandRepresentative rep = optionalRep.get();
            rep.setStatus("APPROVED");
            brandRepRepository.save(rep);

            // User kaydı varsa atla
            Optional<User> existingUser = userRepository.findByEmail(rep.getContactEmail());
            if (existingUser.isEmpty()) {
                User newUser = new User();
                newUser.setEmail(rep.getContactEmail());
                newUser.setFirstName(rep.getFirstName());
                newUser.setLastName(rep.getLastName());
                newUser.setRole("BRAND_REPRESENTATIVE");
                newUser.setActive(true);
                userRepository.save(newUser);
            }

            return Optional.of(rep);
        }
        return Optional.empty();
    }

    // ✅ Reddetme işlemi
    public Optional<BrandRepresentative> reject(Long id) {
        Optional<BrandRepresentative> optionalRep = brandRepRepository.findById(id);
        if (optionalRep.isPresent()) {
            BrandRepresentative rep = optionalRep.get();
            rep.setStatus("REJECTED");
            brandRepRepository.save(rep);
            return Optional.of(rep);
        }
        return Optional.empty();
    }

    public List<BrandRepresentative> getAll() {
        return brandRepRepository.findAll();
    }

    public Optional<BrandRepresentative> getById(Long id) {
        return brandRepRepository.findById(id);
    }

    public Optional<BrandRepresentative> update(Long id, BrandRepresentative updatedData) {
        return brandRepRepository.findById(id).map(existing -> {
            existing.setCompanyName(updatedData.getCompanyName());
            existing.setBrandName(updatedData.getBrandName());
            existing.setFirstName(updatedData.getFirstName());
            existing.setLastName(updatedData.getLastName());
            existing.setContactEmail(updatedData.getContactEmail());
            existing.setCountry(updatedData.getCountry());
            existing.setPhoneNumber(updatedData.getPhoneNumber());
            return brandRepRepository.save(existing);
        });
    }

}
