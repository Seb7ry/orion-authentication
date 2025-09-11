package com.unibague.gradework.orionauth.authentication;

import com.unibague.gradework.orionauth.user.models.UserLogDTO;
import com.unibague.gradework.orionauth.user.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced authentication service with RS256 JWT support
 * Handles login, token generation and user response building
 */
@Slf4j
@Service
public class AuthService implements IAuthService {

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserResponseBuilder userResponseBuilder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> authenticate(LoginRequest loginRequest) {
        log.info("Authentication attempt for email: {}", loginRequest.getEmail());

        try {
            // Get user by email
            UserLogDTO user = userService.getUserByEmail(loginRequest.getEmail());

            // Validate password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                log.warn("Invalid password for user: {}", loginRequest.getEmail());
                throw new IllegalArgumentException("Credenciales inválidas");
            }

            // Build user response
            Map<String, Object> response = userResponseBuilder.buildUserResponse(user);

            // Generate tokens
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            // Add token information to response
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 3600); // seconds

            // Add authentication metadata
            response.put("authenticationTime", System.currentTimeMillis());
            response.put("algorithm", "RS256");

            log.info("Authentication successful for user: {} ({})",
                    user.getIdUser(), loginRequest.getEmail());

            return response;

        } catch (IllegalArgumentException e) {
            // Re-throw validation errors
            throw e;
        } catch (Exception e) {
            log.error("Authentication error for {}: {}", loginRequest.getEmail(), e.getMessage());
            throw new RuntimeException("Error durante la autenticación: " + e.getMessage());
        }
    }

    /**
     * Validate token and return user information (for gateway)
     */
    @Override
    public Map<String, Object> validateToken(String token) {
        try {
            Map<String, Object> claims = jwtUtil.getAllClaims(token);

            Map<String, Object> validation = new HashMap<>();
            validation.put("valid", true);
            validation.put("userId", claims.get("userId"));
            validation.put("email", claims.get("sub"));
            validation.put("role", claims.get("role"));
            validation.put("programs", claims.get("programs"));
            validation.put("permissions", claims.get("permissions"));

            return validation;

        } catch (Exception e) {
            Map<String, Object> validation = new HashMap<>();
            validation.put("valid", false);
            validation.put("error", e.getMessage());
            return validation;
        }
    }

    /**
     * Refresh access token using refresh token
     */
    @Override
    public Map<String, Object> refreshAccessToken(String refreshToken) {
        try {
            if (!jwtUtil.isTokenValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
                throw new IllegalArgumentException("Invalid refresh token");
            }

            String email = jwtUtil.extractEmail(refreshToken);
            UserLogDTO user = userService.getUserByEmail(email);

            String newAccessToken = jwtUtil.generateAccessToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 3600);
            response.put("refreshedAt", System.currentTimeMillis());

            log.info("Token refreshed successfully for user: {}", user.getIdUser());
            return response;

        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }
}