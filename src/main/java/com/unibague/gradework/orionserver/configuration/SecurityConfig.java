package com.unibague.gradework.orionserver.configuration;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Security configuration for the application.
 * Configures authentication, authorization, JWT validation, and CORS settings.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Configures security settings including OAuth2 login and JWT authentication.
     *
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API security
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/oauth2/**").permitAll() // Allow authentication routes
                        .anyRequest().authenticated() // Protect all other routes
                )
                .oauth2Login(oauth2 -> oauth2 // Configure OAuth2 Login with Google
                        .defaultSuccessUrl("http://localhost:5173/dashboard", true) // Redirect after login
                        .failureUrl("http://localhost:5173/login?error=true")
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler()) // Handle logout
                );

        return http.build();
    }

    /**
     * Handles Google logout redirection.
     *
     * @return The configured OidcClientInitiatedLogoutSuccessHandler.
     */
    @Bean
    public OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler successHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("http://localhost:5173/");
        return successHandler;
    }

    /**
     * Configures JWT decoding and validation.
     *
     * @return The configured JwtDecoder.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String secret = System.getProperty("JWT_SECRET");

        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(" ERROR: JWT_SECRET environment variable is missing or too short. " +
                    "Ensure it is at least 32 characters long.");
        }

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    /**
     * Provides password encoding using BCrypt.
     *
     * @return The configured PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures CORS settings.
     *
     * @return The configured CorsConfigurationSource.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
