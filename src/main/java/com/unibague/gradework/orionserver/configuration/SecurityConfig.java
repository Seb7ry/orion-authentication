package com.unibague.gradework.orionserver.configuration;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;

/**
 * Configuration class for setting up application security.
 * Includes configuration for JWT-based authentication, password encoding,
 * and access control rules.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Configures the security filter chain for handling HTTP requests.
     * Defines authorization rules and sets up JWT as the authentication mechanism.
     *
     * @param http the {@link HttpSecurity} object to configure.
     * @return the configured {@link SecurityFilterChain}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disables CSRF protection.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/authenticate/**").permitAll() // Public endpoints.
                        .requestMatchers("/student/**").hasRole("Student") // Restricted to "Student" role.
                        .requestMatchers("/actors/**").hasRole("Actor") // Restricted to "Actor" role.
                        .anyRequest().authenticated() // All other requests require authentication.
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))); // Configures JWT as the resource server authentication mechanism.
        return http.build();
    }

    /**
     * Configures a {@link JwtDecoder} for decoding and validating JWT tokens.
     * Uses a symmetric secret key for signing and verifying tokens.
     *
     * @return the configured {@link JwtDecoder}.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = Keys.hmacShaKeyFor("mySuperSecretKey1234567890123456".getBytes());
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    /**
     * Provides a {@link PasswordEncoder} for encoding and verifying passwords.
     * Uses BCrypt as the encoding algorithm.
     *
     * @return the configured {@link PasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}