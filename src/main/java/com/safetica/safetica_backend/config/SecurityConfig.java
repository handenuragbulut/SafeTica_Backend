package com.safetica.safetica_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.safetica.safetica_backend.security.JwtFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.safetica.safetica_backend.util.JwtUtil;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtUtil jwtUtil, JwtFilter jwtFilter) {
        this.jwtUtil = jwtUtil;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/google-login").permitAll()
                        .requestMatchers("/api/representatives/apply").permitAll()
                        .requestMatchers("/api/ingredients/**").permitAll()
                        .requestMatchers("/api/profiles/**").permitAll()
                        .requestMatchers("/api/favorites/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/favorites/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/error").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/saved-articles/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/saved-articles").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/saved-articles/**").authenticated()

                        .requestMatchers("/api/blog").permitAll()
                        .requestMatchers("/api/blog/**").permitAll()
                        .requestMatchers("/api/scanning-history/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/auth/update/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/profiles/{userId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/{userId}").permitAll()
                        .requestMatchers("/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**")
                        .permitAll()
                        // ✅ Kullanıcıya açık ürün listeleme ve detaylar
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()

                        // ✅ Admin özel işlemleri
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // ✅ Diğer admin endpointleri
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/representatives/**").hasRole("ADMIN")
                        .anyRequest().authenticated());

        // 🔒 JwtFilter ekleniyor
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
