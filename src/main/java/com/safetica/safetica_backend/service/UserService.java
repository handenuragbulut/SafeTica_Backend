package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.dto.UserResponse;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder; 
     @Autowired
    private EmailService emailService; 
    // Method to hash passwords
    public String getPasswordHash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Save user with hashed password
    public void saveUser(User user) {
        userRepository.save(user);
    }
    public User registerUser(String email, String password) {
        User user = new User();
        user.setEmail(email);

        // Şifreyi hash’le
        user.setPassword(passwordEncoder.encode(password));

        // Kullanıcı aktif değil
        user.setActive(false);

        // 6 haneli basit bir doğrulama kodu (OTP)
        String verificationCode = String.format("%06d", (int)(Math.random() * 1000000));
        user.setVerificationCode(verificationCode);

         // Kullanıcıyı kaydet
        userRepository.save(user);

        // Email gönder
        emailService.sendVerificationEmail(email, verificationCode);

        return user;
    }  
    public boolean verifyEmail(String email, String code) {
    Optional<User> optionalUser = userRepository.findByEmail(email);
    if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        if (user.getVerificationCode().equals(code)) {
            user.setActive(true);
            user.setVerificationCode(null); // kodu sıfırla
            userRepository.save(user);
            return true;
        }
    }
    return false;
}

    // Authenticate user by comparing hashed passwords
    public boolean authenticate(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return BCrypt.checkpw(password, user.get().getPasswordHash()); // Verify password
        }
        return false;
    }
    
    

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    public void updateUserInfo(Long userId, String country, String phoneNumber, LocalDate birthDate) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setCountry(country);
            user.setPhoneNumber(phoneNumber);
            user.setBirthDate(birthDate);
            userRepository.save(user); // 🔁 Güncelleme burada yapılır
        }
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserResponse response = new UserResponse();
                    response.setId(user.getId());
                    response.setEmail(user.getEmail());
                    response.setFirstName(user.getFirstName());
                    response.setLastName(user.getLastName());
                    response.setCountry(user.getCountry());
                    response.setPhoneNumber(user.getPhoneNumber());
                    response.setBirthDate(user.getBirthDate());
                    response.setAuthProvider(user.getAuthProvider());
                    response.setRole(user.getRole());
                    response.setActive(user.isActive());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public Optional<User> updateUserRole(Long id, String role) {
        return userRepository.findById(id).map(user -> {
            user.setRole(role);
            userRepository.save(user);
            return user;
        });
    }

    public Optional<User> updateUserStatus(Long id, boolean active) {
        return userRepository.findById(id).map(user -> {
            user.setActive(active);
            userRepository.save(user);
            return user;
        });
    }
}