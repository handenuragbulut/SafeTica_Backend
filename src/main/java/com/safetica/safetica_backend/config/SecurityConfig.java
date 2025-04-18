package com.safetica.safetica_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF korumasını devre dışı bırak
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS yapılandırmasını uygula
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // /api/auth/** yollarını herkese açık yap
                .requestMatchers("/api/products/**").permitAll() // Ürün API'lerini herkese açık yap
                .requestMatchers("/api/google-login").permitAll() // Google Login API'ye izin verildi
                .requestMatchers("/api/ingredients/**").permitAll() //...ingredient sayfası için
                .anyRequest().authenticated() // Diğer yollar kimlik doğrulama gerektirir
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // Çerezleri desteklemek için true
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // React uygulamasının URL'si
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);
        
        return source;
    }
}
