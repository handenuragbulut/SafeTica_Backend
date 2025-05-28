package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.dto.LoginRequest;
import com.safetica.safetica_backend.dto.RegisterRequest;
import com.safetica.safetica_backend.dto.UserResponse;
import com.safetica.safetica_backend.dto.GoogleLoginRequest;
import com.safetica.safetica_backend.dto.GoogleUser;
import com.safetica.safetica_backend.dto.UserUpdateRequest;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.service.GoogleAuthService;
import com.safetica.safetica_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
     * Kullanıcı email & şifre ile giriş
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOptional = userService.findByEmail(loginRequest.getEmail());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            User user = userOptional.get();

            if (!user.getRole().equals("USER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Not a user account.");
            }

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
            }

            // Kullanıcı hesabı devre dışı bırakılmışsa
            if (!user.isActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hesabınız devre dışı bırakıldı.");
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());

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

    @PostMapping("/representative-login")
    public ResponseEntity<?> representativeLogin(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = userOpt.get();

        if (!user.getRole().equals("BRAND_REPRESENTATIVE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Not a representative.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is inactive.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", response);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOptional = userService.findByEmail(loginRequest.getEmail());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            User user = userOptional.get();
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
            }

            if (!user.getRole().equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Not an admin account");
            }

            if (!user.isActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is disabled");
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", response);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while logging in.");
        }
    }

    /**
     * Kullanıcı kayıt
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        try {
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match!");
            }

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
                return ResponseEntity.badRequest().body("Terms must be accepted.");
            }

            User user = new User();
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setEmail(registerRequest.getEmail());
            user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
            user.setBirthDate((LocalDate) registerRequest.getBirthDate());
            user.setPhoneNumber(registerRequest.getPhoneNumber());
            user.setCountry(registerRequest.getCountry());
            user.setAuthProvider("email");
            user.setTermsAccepted(registerRequest.isTermsAccepted());
            user.setCreatedAt(LocalDateTime.now());
            user.setActive(true);
            user.setRole("USER");

            userService.saveUser(user);
            return ResponseEntity.ok("Registration successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Google ile giriş
     */
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest) {
        try {
            GoogleUser googleUser = googleAuthService.decodeGoogleToken(googleLoginRequest.getGoogleIdToken());

            Optional<User> userOptional = userService.findByGoogleId(googleUser.getGoogleId());
            User user = userOptional.orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(googleUser.getEmail());
                newUser.setGoogleId(googleUser.getGoogleId());
                newUser.setAuthProvider("google");
                newUser.setCreatedAt(LocalDateTime.now());
                userService.saveUser(newUser);
                return newUser;
            });

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", response);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google ID Token");
        }
    }

    /**
     * Kullanıcı bilgisi (JWT ile)
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.getEmailFromToken(token);
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCountry(user.getCountry());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setBirthDate(user.getBirthDate() != null ? user.getBirthDate().toString() : null);
        response.setAuthProvider(user.getAuthProvider());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UserUpdateRequest request) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
        }

        String email = jwtUtil.getEmailFromToken(token);
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        try {
            User user = optionalUser.get();

            // Güncelleme işlemleri
            if (request.getCountry() != null) {
                user.setCountry(request.getCountry());
            }

            if (request.getPhoneNumber() != null) {
                user.setPhoneNumber(request.getPhoneNumber());
            }

            if (request.getBirthDate() != null) {
                user.setBirthDate(request.getBirthDate());
            }

            userService.saveUser(user); // güncellenmiş kullanıcıyı kaydet

            return ResponseEntity.ok("User info updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Update failed: " + e.getMessage());
        }
    }

}
