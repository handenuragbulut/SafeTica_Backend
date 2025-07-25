package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.dto.UserRegistrationRequest;
import com.safetica.safetica_backend.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import com.safetica.safetica_backend.entity.User;
import com.safetica.safetica_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/verify-email")
    public boolean verifyEmail(@RequestParam String email, @RequestParam String code) {
        return userService.verifyEmail(email, code);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @CrossOrigin // test için
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        userService.registerUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok("Kullanici basariyla kaydedildi ve e-posta gonderildi!");
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        return userService.updateUserRole(id, role)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        return userService.updateUserStatus(id, active)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
