package com.safetica.safetica_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF korumasını devre dışı bırak
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS yapılandırmasını uygula
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // /api/auth/** yollarını herkese açık yap
                .requestMatchers("/api/products/**").permitAll() // /api/products/** yollarını herkese açık yap
                .anyRequest().authenticated() // Diğer tüm yollar kimlik doğrulama gerektirir
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // Kimlik bilgilerine izin ver
        config.addAllowedOrigin("http://localhost:3000"); // React uygulamasının URL'si
        config.addAllowedHeader("*"); // Tüm başlıklara izin ver
        config.addAllowedMethod("*"); // Tüm HTTP metodlarına izin ver (GET, POST, PUT, DELETE, vb.)

        source.registerCorsConfiguration("/**", config); // Tüm endpoint'lere CORS yapılandırmasını uygula
        return source;
    }
}
