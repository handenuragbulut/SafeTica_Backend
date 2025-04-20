package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.dto.LoginRequest;
import com.safetica.safetica_backend.dto.RegisterRequest;
import com.safetica.safetica_backend.dto.UserResponse;
import com.safetica.safetica_backend.dto.GoogleLoginRequest;
import com.safetica.safetica_backend.dto.GoogleUser;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.service.GoogleAuthService;
import com.safetica.safetica_backend.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private com.safetica.safetica_backend.util.JwtUtil jwtUtil;

    /**
     * Kullanıcı giriş (email ve şifre ile)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOptional = userService.findByEmail(loginRequest.getEmail());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            User user = userOptional.get();

            // Şifre doğrulama
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
            }

            // Token oluştur
            String token = jwtUtil.generateToken(user.getEmail());

            // Kullanıcı bilgilerini DTO olarak hazırla
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());

            // HashMap ile response oluştur
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", response);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while logging in.");
        }
    }

    /**
     * Form ile kullanıcı kaydı
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // Şifre eşleşmesini kontrol et
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match!");
            }

            // Zorunlu alanların kontrolü
            if (registerRequest.getFirstName() == null || registerRequest.getFirstName().isEmpty()) {
                return ResponseEntity.badRequest().body("First name is required.");
            }
            if (registerRequest.getLastName() == null || registerRequest.getLastName().isEmpty()) {
                return ResponseEntity.badRequest().body("Last name is required.");
            }
            if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required.");
            }
            if (registerRequest.getBirthDate() == null) {
                return ResponseEntity.badRequest().body("Birth date is required.");
            }
            if (registerRequest.getCountry() == null || registerRequest.getCountry().isEmpty()) {
                return ResponseEntity.badRequest().body("Country is required.");
            }
            if (!registerRequest.isTermsAccepted()) {
                return ResponseEntity.badRequest().body("Terms and conditions must be accepted.");
            }

            // Yeni kullanıcı oluştur
            User user = new User();
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setEmail(registerRequest.getEmail());
            user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword())); // Şifre hashleme
            user.setBirthDate((LocalDate) registerRequest.getBirthDate());
            user.setPhoneNumber(registerRequest.getPhoneNumber());
            user.setCountry(registerRequest.getCountry());
            user.setAuthProvider("email"); // Form ile kayıt
            user.setTermsAccepted(registerRequest.isTermsAccepted());
            user.setCreatedAt(LocalDateTime.now());

            userService.saveUser(user);
            return ResponseEntity.ok("Registration successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Google Login Endpoint
     */
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest) {
        try {
            // Google token'ı doğrula ve kullanıcı bilgilerini al
            GoogleUser googleUser = googleAuthService.decodeGoogleToken(googleLoginRequest.getGoogleIdToken());

            // Kullanıcı Google ID ile veritabanında mevcut mu kontrol et
            Optional<User> userOptional = userService.findByGoogleId(googleUser.getGoogleId());

            User user;
            if (userOptional.isPresent()) {
                user = userOptional.get();
            } else {
                // Kullanıcı bulunamadıysa yeni bir kullanıcı oluştur
                user = new User();
                user.setEmail(googleUser.getEmail());
                user.setGoogleId(googleUser.getGoogleId());
                user.setAuthProvider("google");
                user.setCreatedAt(LocalDateTime.now());
                userService.saveUser(user);
            }

            // Token oluştur
            String token = jwtUtil.generateToken(user.getEmail());

            // User DTO oluştur
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());

            // HashMap ile token + user bilgisi dön
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", response);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google ID Token");
        }
    }

    /**
     * Google Callback Endpoint
     */
    @GetMapping("/google/callback")
    public void googleCallback(@RequestParam("id_token") String idToken, HttpServletResponse response) {
        try {
            // Google Token'ı doğrula ve kullanıcı bilgilerini al
            GoogleUser googleUser = googleAuthService.decodeGoogleToken(idToken);

            // Kullanıcı Google ID ile kontrol edilerek işlem yapılır
            Optional<User> userOptional = userService.findByGoogleId(googleUser.getGoogleId());
            if (userOptional.isPresent()) {
                // Kullanıcı mevcutsa frontend'e başarılı giriş yönlendirmesi yapılır
                response.sendRedirect("http://localhost:3000/home?status=success");
            } else {
                // Kullanıcı mevcut değilse yeni bir kullanıcı oluşturulur
                User newUser = new User();
                newUser.setEmail(googleUser.getEmail());
                newUser.setGoogleId(googleUser.getGoogleId());
                newUser.setAuthProvider("google");
                newUser.setCreatedAt(LocalDateTime.now());
                userService.saveUser(newUser);

                // Yeni kullanıcı kaydedildiği bilgisiyle yönlendirme yapılır
                response.sendRedirect("http://localhost:3000/home?status=new_user");
            }
        } catch (Exception e) {
            // Hata durumunda login sayfasına hata mesajıyla yönlendirme yapılır
            try {
                response.sendRedirect("http://localhost:3000/login?status=error");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
